package controllers;

import models.AccountingPeriod;
import models.BalanceSheet;
import models.Money;
import models.ProfitAndLossAccount;
import play.i18n.Messages;

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

        BalanceSheet balanceSheet = new BalanceSheet(accountingPeriod);

        Money profitDifference = balanceSheet.getProfit().subtract(profitAndLossAccount.getProfit());

        if(profitDifference.value != 0l) {
            flash.error(Messages.get("profitAndLossAccount.profitDifference", profitDifference));
        }

        List<AccountingPeriod> accountingPeriods = AccountingPeriod.findAll();

        render(profitAndLossAccount, accountingPeriods);
    }
}
