package controllers;

import models.AccountingPeriod;
import models.Entry;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class Entries extends ApplicationController {
    public static void index(Long accountingPeriodId, int page, String orderBy, String order, String search) {

        AccountingPeriod currentAccountingPeriod;
        if(accountingPeriodId == null) {
            currentAccountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        } else {
            currentAccountingPeriod = AccountingPeriod.findById(accountingPeriodId);
        }
        notFoundIfNull(currentAccountingPeriod);
        
        String where = String.format("accountingPeriod.id = %s", currentAccountingPeriod.id);
        
        if (page < 1) {
            page = 1;
        }
        
        List<String> properties = new ArrayList<String>();
        properties.add("description");
        properties.add("voucher");
        
        List<Model> entries = Model.Manager.factoryFor(Entry.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                properties,
                search,
                where
        );

        Long count = Model.Manager.factoryFor(Entry.class).count(properties, search, where);

        renderArgs.put("pageSize", getPageSize());
        initRenderArgs(); // accounting periods
        render(entries, count, currentAccountingPeriod);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Entry entry = Entry.findById(id);
        notFoundIfNull(entry);
        render(entry);
    }
    
    public static void form() {
        initRenderArgs();
        render();
    }
    
    public static void save(@Valid Entry entry) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", entry);
        }

        entry.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("entry")));
        index(null, 1, null, null, null);
    }

    public static void cancelModal(Long id) {
        notFoundIfNull(id);
        Entry entry = Entry.findById(id);
        notFoundIfNull(entry);
        render(entry);
    }

    public synchronized static void cancel(Long id) {
        notFoundIfNull(id);
        Entry entry = Entry.findById(id);
        notFoundIfNull(entry);
        
        // sanity check
        if(!entry.isCancelable()) {
            flash.error(Messages.get("entry.entryIsCanceled"));
            show(entry.id);
        }
        
        Entry reverseEntry = entry.buildReverseEntry();
        reverseEntry.loggedSave(getCurrentUser());
        
        flash.success(Messages.get("successfullyCreated", Messages.get("entry.reverseEntry")));
        index(null, 1, null, null, null);
    }

    private static void initRenderArgs() {
        renderArgs.put("accountingPeriods", AccountingPeriod.findAll());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
