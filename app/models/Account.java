package models;

import play.Play;
import util.i18n.CurrencyProvider;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account extends EnhancedModel {

    public static Account getDebitorAccount() {
        return getAccountForConfigurationKey("accounting.debitorAccount.number");
    }

    public static Account getRevenueAccount() {
        return getAccountForConfigurationKey("accounting.revenueAccount.number");
    }

    public static Account getValueAddedTaxAccount() {
        return getAccountForConfigurationKey("accounting.valueAddedTaxAccount.number");
    }

    public static Account getDiscountAccount() {
        return getAccountForConfigurationKey("accounting.discountAccount.number");
    }

    public static Account getChargeOffAccount() {
        return getAccountForConfigurationKey("accounting.chargeOffAccount.number");
    }

    public static Account getCreditorAccount() {
        return getAccountForConfigurationKey("accounting.creditorAccount.number");
    }

    public static Account getPurchasesAccount() {
        return getAccountForConfigurationKey("accounting.purchasesAccount.number");
    }

    public static Account getPurchaseDiscountAccount() {
        return getAccountForConfigurationKey("accounting.purchaseDiscountAccount.number");
    }

    public static List<Account> getPaymentAccounts() {
        return getAccountsForConfigurationKey("accounting.paymentAccounts.numbers");
    }

    public static List<Account> getInputTaxAccounts() {
        return getAccountsForConfigurationKey("accounting.inputTaxAccounts.numbers");
    }
    
    private static Account getAccountForConfigurationKey(String configurationKey) {
        String debitorAccountNumber = Play.configuration.getProperty(configurationKey);
        Account account = Account.find("number = ?", debitorAccountNumber).first();
        if(account == null) {
            throw new RuntimeException("Unable to find account");
        }
        return account;
    } 
    
    private static List<Account> getAccountsForConfigurationKey(String configurationKey) {
        List<Account> accounts = new ArrayList<Account>();
        String accountNumbers = Play.configuration.getProperty(configurationKey);
        for(String accountNumber : accountNumbers.split(",")) {
            accountNumber = accountNumber.trim();
            Account account = Account.find("number = ?", accountNumber).first();
            if(account == null) {
                throw new RuntimeException("Unable to find account");
            }    
            accounts.add(account);
        }
        
        return accounts;
    } 
    
    public String number;

    public String description;

    @ManyToOne
    public AccountGroup accountGroup;

    public String getLabel() {
        return String.format("%s %s", number, description);
    }
    
    public List<Entry> getDebitEntries(AccountingPeriod accountingPeriod) {
        return Entry.find("debit = ? AND accountingPeriod = ?", this, accountingPeriod).fetch();
    }

    public Money getDebitSum(AccountingPeriod accountingPeriod) {
        Money sum = new Money(CurrencyProvider.getDefaultCurrency());
        for(Entry entry : getDebitEntries(accountingPeriod)) {
            sum = sum.add(entry.amount);
        }
        return sum;
    }
    
    public List<Entry> getCreditEntries(AccountingPeriod accountingPeriod) {
        return Entry.find("credit = ? AND accountingPeriod = ?", this, accountingPeriod).fetch();
    }


    public Money getCreditSum(AccountingPeriod accountingPeriod) {
        Money sum = new Money(CurrencyProvider.getDefaultCurrency());
        for(Entry entry : getCreditEntries(accountingPeriod)) {
            sum = sum.add(entry.amount);
        }
        return sum;
    }

    public Money getBalance(AccountingPeriod accountingPeriod) {
        // check if opening balance exists
        OpeningBalance openingBalance = OpeningBalance.find("account.id = ?", id).first();

        Money balance;
        if(openingBalance != null) {
            balance = new Money(openingBalance.openingBalance);
        } else {
            balance = new Money(CurrencyProvider.getDefaultCurrency());
        }
        
        if(accountGroup.accountType == AccountType.ASSET ||
           accountGroup.accountType == AccountType.LIABILITY) {
            // debit account
            balance = balance.add(getDebitSum(accountingPeriod));
            balance = balance.subtract(getCreditSum(accountingPeriod));
        } else {
            // credit account
            balance = balance.add(getCreditSum(accountingPeriod));
            balance = balance.subtract(getDebitSum(accountingPeriod));
        }

        return balance;
    }
}
