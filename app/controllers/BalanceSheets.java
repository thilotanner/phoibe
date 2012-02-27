package controllers;

import models.AccountingPeriod;
import models.BalanceSheet;

import java.util.List;

public class BalanceSheets extends ApplicationController {
    public static void index(Long accountingPeriodId) {

        AccountingPeriod accountingPeriod;
        if(accountingPeriodId == null) {
            accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        } else {
            accountingPeriod = AccountingPeriod.findById(accountingPeriodId);
        }
        notFoundIfNull(accountingPeriod);

        BalanceSheet balanceSheet = new BalanceSheet(accountingPeriod);

        List<AccountingPeriod> accountingPeriods = AccountingPeriod.findAll();

        render(balanceSheet, accountingPeriods);
    }
}
