package controllers;

import com.google.common.base.Strings;
import models.Account;
import models.Creditor;
import models.CreditorAttachment;
import models.CreditorStatus;
import models.DebitorStatus;
import models.ValueAddedTaxRate;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;
import util.i18n.CurrencyProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Creditors extends ApplicationController {
    public static void index(String filter, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        if(filter != null && !filter.isEmpty()) {
            try {
                CreditorStatus creditorStatus = CreditorStatus.valueOf(filter);
                qb.must(QueryBuilders.fieldQuery("creditorStatus", creditorStatus.toString()));
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }

        Query<Creditor> query = ElasticSearch.query(qb, Creditor.class);

        query.from((page - 1) * getPageSize()).size(getPageSize());

        if(!Strings.isNullOrEmpty(orderBy)) {
            SortOrder sortOrder = SortOrder.ASC;
            if(!Strings.isNullOrEmpty(order)) {
                if(order.toLowerCase().equals("desc")) {
                    sortOrder = SortOrder.DESC;
                }
            }

            query.addSort(orderBy, sortOrder);
        }

        List<Creditor> creditors;
        Long count;

        try {
            SearchResults<Creditor> results = query.fetch();
            creditors = results.objects;
            count = results.totalCount;
        } catch (SearchPhaseExecutionException e) {
            Logger.warn(String.format("Error in search query: %s", search), e);
            flash.now("warning", Messages.get("errorInSearchQuery"));

            creditors = new ArrayList<Creditor>();
            count = 0l;
        }

        renderArgs.put("pageSize", getPageSize());
        render(creditors, count, filter);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        render(creditor);
    }
    
    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            Creditor creditor = new Creditor();
            creditor.creditorStatus = CreditorStatus.DUE;
            render("@create", creditor);
        }

        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        render(creditor);
    }

    public synchronized static void save(@Valid Creditor creditor, File file) {
        sanityCheck(creditor);

        validation.required("file", file);
        validation.isTrue("creditor.amount.rawValue", 0l < creditor.amount.value).message("validation.invalid");

        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@create", creditor); 
        }

        creditor.buildAndSaveCreditorEntries();
        
        creditor.loggedSave(getCurrentUser());

        // handle file
        if(!creditor.getAttachmentFolder().exists() && !creditor.getAttachmentFolder().mkdirs()) {
            flash.error(Messages.get("attachment.unableToCreateAttachmentFolder"));
            Creditors.index(null, 1, null, null, null);
        }

        String filename = util.file.FileUtils.sanitizeFilename(file.getName());

        File targetFile = new File(creditor.getAttachmentFolder(), filename);
        if(targetFile.exists()) {
            flash.error(Messages.get("attachment.fileExistsAlready"));
            Creditors.index(null, 1, null, null, null);
        }

        try {
            FileUtils.copyFile(file, targetFile);
        } catch (IOException e) {
            flash.error(Messages.get("attachment.unableToCopyFile"));
            Creditors.index(null, 1, null, null, null);
        }

        flash.success(Messages.get("successfullySaved", Messages.get("creditor")));
        show(creditor.id);
    }
    
    public synchronized static void update(@Valid Creditor creditor, File file) {
        sanityCheck(creditor);

        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", creditor);
        }

        if(creditor.id == null) {
            creditor.buildAndSaveCreditorEntries();
        }

        creditor.loggedSave(getCurrentUser());
        
        // handle file
        if(file != null) {
            // delete current attachment
            for(CreditorAttachment attachment : creditor.getAttachments()) {
                if(!attachment.delete()) {
                    flash.error(Messages.get("attachment.unableToDeleteFile"));
                    Creditors.index(null, 1, null, null, null);
                }
            }

            String filename = util.file.FileUtils.sanitizeFilename(file.getName());

            File targetFile = new File(creditor.getAttachmentFolder(), filename);
            if(targetFile.exists()) {
                flash.error(Messages.get("attachment.fileExistsAlready"));
                Creditors.index(null, 1, null, null, null);
            }

            try {
                FileUtils.copyFile(file, targetFile);
            } catch (IOException e) {
                flash.error(Messages.get("attachment.unableToCopyFile"));
                Creditors.index(null, 1, null, null, null);
            }
        }

        flash.success(Messages.get("successfullySaved", Messages.get("creditor")));
        show(creditor.id);
    }

    public static void confirmDiscountAmountDue(Long id) {
        notFoundIfNull(id);
        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        render(creditor);
    }
    
    public synchronized static void discountAmountDue(Long id) {
        notFoundIfNull(id);
        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        sanityCheck(creditor);

        creditor.buildAndSaveDiscountEntries();

        closeCreditor(creditor, "creditor.successfullyDiscounted");
    }

    private static void closeCreditor(Creditor creditor, String messageKey) {
        creditor.close();

        flash.success(Messages.get(messageKey));
        show(creditor.id);
    }

    protected static void sanityCheck(Creditor creditor) {
        // sanity check --> is creditor still due
        if(!creditor.isEditable()) {
            flash.error(Messages.get("creditor.creditorIsPaid"));
            show(creditor.id);
        }
    }

    private static void initRenderArgs() {
        renderArgs.put("paymentAccounts", Account.getPaymentAccounts());
        List<Account> expenseAccounts = new ArrayList<Account>();
        expenseAccounts.addAll(Account.getExpenseAccounts());
        expenseAccounts.addAll(Account.getAdditionalExpenseAccounts());
        renderArgs.put("expenseAccounts", expenseAccounts);
        renderArgs.put("inputTaxAccounts", Account.getInputTaxAccounts());
        renderArgs.put("valueAddedTaxRates", ValueAddedTaxRate.findAllWithZeroRate());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
