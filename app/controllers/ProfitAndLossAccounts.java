package controllers;

import models.AccountingPeriod;
import models.ProfitAndLossAccount;

import java.util.List;

public class ProfitAndLossAccounts extends ApplicationController {
    public static void index(Long accountingPeriodId) {

        AccountingPeriod accountingPeriod;
        if(accountingPeriodId == null) {
            accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        } else {
            accountingPeriod = AccountingPeriod.findById(accountingPeriodId);
        }
        notFoundIfNull(accountingPeriod);

        ProfitAndLossAccount profitAndLossAccount = new ProfitAndLossAccount(accountingPeriod);

        List<AccountingPeriod> accountingPeriods = AccountingPeriod.findAll();

        render(profitAndLossAccount, accountingPeriods);
    }
}
