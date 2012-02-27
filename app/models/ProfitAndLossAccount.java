package models;

import util.i18n.CurrencyProvider;

import java.util.List;

public class ProfitAndLossAccount {

    private AccountingPeriod accountingPeriod;
    List<AccountGroup> expenseAccountGroups;
    List<AccountGroup> revenueAccountGroups;

    public ProfitAndLossAccount(AccountingPeriod accountingPeriod) {
        if(accountingPeriod == null) {
            throw new IllegalArgumentException("Accounting period must not be null!");
        }

        this.accountingPeriod = accountingPeriod;

        expenseAccountGroups = AccountGroup.find("accountType = ?", AccountType.EXPENSE).fetch();
        revenueAccountGroups = AccountGroup.find("accountType = ?", AccountType.REVENUE).fetch();
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public List<AccountGroup> getExpenseAccountGroups() {
        return expenseAccountGroups;
    }

    public List<AccountGroup> getRevenueAccountGroups() {
        return revenueAccountGroups;
    }

    public Money getExpenseBalance() {
        Money balance = new Money(CurrencyProvider.getDefaultCurrency());
        for(AccountGroup accountGroup : expenseAccountGroups) {
            balance = balance.add(accountGroup.getBalance(accountingPeriod));
        }
        return balance;
    }

    public Money getRevenueBalance() {
        Money balance = new Money(CurrencyProvider.getDefaultCurrency());
        for(AccountGroup accountGroup : revenueAccountGroups) {
            balance = balance.add(accountGroup.getBalance(accountingPeriod));
        }
        return balance;
    }
}
