package models;

import util.i18n.CurrencyProvider;

import java.util.List;

public class BalanceSheet {

    private AccountingPeriod accountingPeriod;
    List<AccountGroup> assetAccountGroups;
    List<AccountGroup> liabilityAccountGroups;

    public BalanceSheet(AccountingPeriod accountingPeriod) {
        if(accountingPeriod == null) {
            throw new IllegalArgumentException("Accounting period must not be null!");
        }

        this.accountingPeriod = accountingPeriod;

        assetAccountGroups = AccountGroup.find("accountType = ?", AccountType.ASSET).fetch();
        liabilityAccountGroups = AccountGroup.find("accountType = ?", AccountType.LIABILITY).fetch();
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public List<AccountGroup> getAssetAccountGroups() {
        return assetAccountGroups;
    }

    public List<AccountGroup> getLiabilityAccountGroups() {
        return liabilityAccountGroups;
    }

    public Money getAssetBalance() {
        Money balance = new Money(CurrencyProvider.getDefaultCurrency());
        for(AccountGroup accountGroup : assetAccountGroups) {
            balance = balance.add(accountGroup.getBalance(accountingPeriod));
        }
        return balance;
    }

    public Money getLiabilityBalance() {
        Money balance = new Money(CurrencyProvider.getDefaultCurrency());
        for(AccountGroup accountGroup : liabilityAccountGroups) {
            balance = balance.add(accountGroup.getBalance(accountingPeriod));
        }
        return balance;
    }
}
