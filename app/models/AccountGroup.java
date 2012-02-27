package models;

import util.i18n.CurrencyProvider;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class AccountGroup extends EnhancedModel {

    public String description;

    @Enumerated(EnumType.STRING)
    public AccountType accountType;
    
    @OneToMany(mappedBy = "accountGroup")
    public List<Account> accounts;
    
    public Money getBalance(AccountingPeriod accountingPeriod) {
        Money balance = new Money(CurrencyProvider.getDefaultCurrency());
        for(Account account : accounts) {
            balance = balance.add(account.getBalance(accountingPeriod));
        }
        return balance;
    }
}
