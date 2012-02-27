package controllers;

import models.Account;
import models.AccountingPeriod;
import models.Money;
import models.OpeningBalance;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingPeriods extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> accountingPeriods = Model.Manager.factoryFor(AccountingPeriod.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(AccountingPeriod.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(accountingPeriods, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        AccountingPeriod accountingPeriod = AccountingPeriod.findById(id);
        notFoundIfNull(accountingPeriod);
        render(accountingPeriod);
    }

    public static void form(Long id) {
        AccountingPeriod accountingPeriod;
        if (id == null) {
            accountingPeriod = new AccountingPeriod();
        } else {
            accountingPeriod = AccountingPeriod.findById(id);
            notFoundIfNull(accountingPeriod);
        }

        buildOpeningBalances(accountingPeriod);

        render(accountingPeriod);
    }

    public static void save(@Valid AccountingPeriod accountingPeriod) {
        if(Validation.hasErrors()) {
            render("@form", accountingPeriod);
        }

        OpeningBalance.delete("accountingPeriod.id = ?", accountingPeriod.id);

        // save opening balances
        List<OpeningBalance> openingBalances = new ArrayList<OpeningBalance>();
        for(OpeningBalance openingBalance : accountingPeriod.openingBalances) {
            if(openingBalance != null &&
               openingBalance.openingBalance.value != null &&
               openingBalance.openingBalance.value != 0l) {
                openingBalance.accountingPeriod = accountingPeriod;
                openingBalances.add(openingBalance);
            }
        }
        accountingPeriod.openingBalances = openingBalances;
        
        accountingPeriod.loggedSave(getCurrentUser());
        
        // unactivate other accounting periods
        if(accountingPeriod.active) {
            for(AccountingPeriod otherAccountingPeriod : AccountingPeriod.<AccountingPeriod>findAll()) {
                if(!accountingPeriod.id.equals(otherAccountingPeriod.id) && otherAccountingPeriod.active) {
                    otherAccountingPeriod.active = false;
                    otherAccountingPeriod.save();
                }
            }
        }
        
        flash.success(Messages.get("successfullySaved", Messages.get("accountingPeriod")));
        index(1, null, null, null);
    }
    
    private static void buildOpeningBalances(AccountingPeriod accountingPeriod) {
        // build map of existing opening balances
        Map<Long, OpeningBalance> existingOpeningBalances = new HashMap<Long, OpeningBalance>();
        if(accountingPeriod.openingBalances != null) {
            for(OpeningBalance openingBalance : accountingPeriod.openingBalances) {
                existingOpeningBalances.put(openingBalance.account.id, openingBalance);
            }
        }

        List<OpeningBalance> openingBalances = new ArrayList<OpeningBalance>();
        for(Account account : Account.<Account>findAll()) {
            OpeningBalance openingBalance = new OpeningBalance();
            openingBalance.account = account;
            openingBalance.accountingPeriod = accountingPeriod;

            if(existingOpeningBalances.containsKey(account.id)) {
                openingBalance.openingBalance = existingOpeningBalances.get(account.id).openingBalance;
            } else {
                openingBalance.openingBalance = new Money(CurrencyProvider.getDefaultCurrency());
            }

            openingBalances.add(openingBalance);
        }
        accountingPeriod.openingBalances = openingBalances;
    }
}
