import models.Account;
import models.AccountType;
import models.AccountingPeriod;
import models.BalanceSheet;
import models.Entry;
import models.Money;
import models.ProfitAndLossAccount;
import org.junit.Assert;
import org.junit.Test;
import play.Logger;
import play.test.UnitTest;
import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountingTest extends UnitTest {
    
    @Test
    public void profitTest() {
        
        // build a list of 4 accounts, one of each account type
        List<Account> accounts = new ArrayList<Account>();
        accounts.add(Account.find("accountGroup.accountType = ?", AccountType.ASSET).<Account>first());
        accounts.add(Account.find("accountGroup.accountType = ?", AccountType.LIABILITY).<Account>first());
        accounts.add(Account.find("accountGroup.accountType = ?", AccountType.EXPENSE).<Account>first());
        accounts.add(Account.find("accountGroup.accountType = ?", AccountType.REVENUE).<Account>first());
        
        for(Account debit : accounts) {
            for(Account credit : accounts) {
                entry(debit, credit);
                entry(credit, debit);
            }
        }
    }

    private void entry(Account debit, Account credit) {
        Entry entry = new Entry();
        entry.debit = debit;
        entry.credit = credit;
        entry.date = new Date();
        Money amount = new Money(CurrencyProvider.getDefaultCurrency());
        amount.setRawValue("100");
        entry.amount = amount;
        entry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        entry.description = String.format("%s - %s", debit.getLabel(), credit.getLabel());
        entry.save();

        Logger.info(String.format("Entry created: %s - %s", entry.debit.getLabel(), entry.credit.getLabel()));

        // build balance sheet
        BalanceSheet balanceSheet = new BalanceSheet(AccountingPeriod.getActiveAccountingPeriod());

        // build profit and loss account
        ProfitAndLossAccount profitAndLossAccount = new ProfitAndLossAccount(AccountingPeriod.getActiveAccountingPeriod());

        // compare profit
        Assert.assertEquals(balanceSheet.getProfit().value, profitAndLossAccount.getProfit().value);
        Logger.info(String.format("Profit is equal: %s", balanceSheet.getProfit()));
    }
}
