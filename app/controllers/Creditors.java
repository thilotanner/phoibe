package controllers;

import models.Account;
import models.Creditor;
import models.CreditorStatus;
import models.DebitorStatus;
import models.ValueAddedTaxRate;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class Creditors extends ApplicationController {
    public static void index(String filter, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        String where = null;
        for(DebitorStatus debitorStatus : DebitorStatus.values()) {
            if(debitorStatus.toString().equals(filter)) {
                where = String.format("creditorStatus = '%s'", filter);
            }
        }

        List<Model> creditors = Model.Manager.factoryFor(Creditor.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                where
        );

        Long count = Model.Manager.factoryFor(Creditor.class).count(new ArrayList<String>(), search, where);

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

    public synchronized static void save(@Valid Creditor creditor) {
        sanityCheck(creditor);

        if(Validation.hasErrors()) {
            initRenderArgs();
            if(creditor.id == null) {
                render("@create", creditor);
            } else {
                render("@form", creditor);
            }
        }

        if(creditor.id == null) {
            creditor.buildAndSaveCreditorEntries();
        }

        creditor.loggedSave(getCurrentUser());

        flash.success(Messages.get("successfullySaved", Messages.get("creditor")));
        index(CreditorStatus.DUE.toString(), 1, null, null, null);
    }

    public synchronized static void discountAmountDue(Long creditorId) {
        notFoundIfNull(creditorId);
        Creditor creditor = Creditor.findById(creditorId);
        notFoundIfNull(creditor);

        sanityCheck(creditor);

        creditor.buildAndSaveDiscountEntries();

        closeCreditor(creditor, "creditor.successfullyDiscounted");
    }

    private static void closeCreditor(Creditor creditor, String messageKey) {
        creditor.close();

        flash.success(Messages.get(messageKey));
        index(CreditorStatus.DUE.toString(), 1, null, null, null);
    }

    protected static void sanityCheck(Creditor creditor) {
        // sanity check --> is creditor still due
        if(creditor.creditorStatus.equals(CreditorStatus.PAID)) {
            flash.error(Messages.get("creditor.creditorIsPaid"));
            show(creditor.id);
        }
    }

    private static void initRenderArgs() {
        renderArgs.put("paymentAccounts", Account.getPaymentAccounts());
        renderArgs.put("inputTaxAccounts", Account.getInputTaxAccounts());
        renderArgs.put("valueAddedTaxRates", ValueAddedTaxRate.findAllWithZeroRate());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
