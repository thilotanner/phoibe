package models;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPABase;
import play.i18n.Messages;
import util.hashing.HashingUtils;
import util.i18n.CurrencyProvider;
import util.string.NonEmptyStringBuilder;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Entry extends EnhancedModel {

    private static final String CHECKSUM_DELIMITER = "|";
    private static final DateFormat CHECKSUM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    @ManyToOne
    public AccountingPeriod accountingPeriod;

    @Required
    @Temporal(TemporalType.DATE)
    public Date date;

    @Required
    public String description;

    public String voucher;

    @Required
    @ManyToOne
    public Account debit;

    @Required
    @ManyToOne
    public Account credit;

    @Valid
    @Embedded
    public Money amount;
    
    public String checksum;
    
    @ManyToOne
    public Entry canceledEntry;
    
    @Transient
    Map<AccountingPeriod, Map<Account, Money>> balanceCache;
    
    public String calculateChecksum() {
        Entry entry = null;
        if(id == null) {
            // get entry with highest id 
            entry = Entry.find("order by id DESC").first();
        } else if (id > 1) {
            // get previous entry
            entry = Entry.findById(id - 1);
        }
        
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.setDelimiter(CHECKSUM_DELIMITER);
        if(entry != null) {
            nesb.append(entry.checksum);
        }
        nesb.append(accountingPeriod.description);
        nesb.append(CHECKSUM_DATE_FORMAT.format(date.getTime()));
        nesb.append(description);
        nesb.append(voucher);

        // load accounts from session to have all fields available
        // for checksum calculation
        debit = Account.findById(debit.id);
        credit = Account.findById(credit.id);

        nesb.append(debit.number);
        nesb.append(credit.number);
        nesb.append(amount.toString());
        
        return HashingUtils.calculateSHA(nesb.toString());
    }
    
    public boolean isValid() {
        return checksum.equals(calculateChecksum());    
    }

    public boolean isCancelable() {
        return !(canceledEntry != null || Entry.count("canceledEntry.id = ?", id) > 0);
    }

    public Entry buildReverseEntry() {
        Entry reverseEntry = new Entry();
        reverseEntry.accountingPeriod = accountingPeriod;
        reverseEntry.date = date;
        reverseEntry.description = String.format("%s: %s", Messages.get("entry.reverseEntry"), description);
        reverseEntry.voucher = voucher;
        reverseEntry.debit = credit;
        reverseEntry.credit = debit;
        reverseEntry.amount = amount;
        reverseEntry.canceledEntry = this;
        return reverseEntry;
    }
    
    public Entry getPreviousEntry(AccountingPeriod accountingPeriod, Account account) {
        if(!debit.id.equals(account.id) && !credit.id.equals(account.id)) {
            throw new IllegalArgumentException("Account must either match debit or credit");
        }

        // entries are sorted by date asc, id asc
        return Entry.find(String.format("accountingPeriod = ? and (debit = ? or credit = ?) and (date < ? or (date = ? and id < ?)) order by date desc, id desc"),
                accountingPeriod, account, account, this.date, this.date, this.id).first();
    }
    
    public Money getBalance(AccountingPeriod accountingPeriod, Account account) {
        if(debit != account && credit != account) {
            throw new IllegalArgumentException("Account must either match debit or credit");
        }

        if(balanceCache != null) {
            Map<Account, Money> balanceMap = balanceCache.get(accountingPeriod);
            if(balanceMap != null) {
                if(balanceMap.containsKey(account)) {
                    return balanceMap.get(account);
                }
            }
        }
        
        Money balance;

        // ask previous entry for balance
        Entry previousEntry = getPreviousEntry(accountingPeriod, account);

        if(previousEntry != null) {
            // previous entry exists --> ask that entry for the balance
            balance = new Money(previousEntry.getBalance(accountingPeriod, account));
        } else {
            // this must be the first entry --> check if opening balance exists
            OpeningBalance openingBalance = account.getOpeningBalance(accountingPeriod);

            if(openingBalance != null) {
                balance = new Money(openingBalance.openingBalance);
            } else {
                balance = new Money(CurrencyProvider.getDefaultCurrency());
            }
        }
        
        // add / subtract amount of this entry

        if(account.accountGroup.accountType.isDebitAccount()) {
            // debit account
            if(account.equals(debit)) {
                balance = balance.add(amount);
            } else {
                balance = balance.subtract(amount);
            }
        } else {
            // credit account
            if(account.equals(debit)) {
                balance = balance.subtract(amount);
            } else {
                balance = balance.add(amount);
            }
        }

        // add to cache
        if(balanceCache == null) {
            balanceCache = new HashMap<AccountingPeriod, Map<Account, Money>>();
        }
        Map<Account, Money> balanceMap;
        if(balanceCache.containsKey(accountingPeriod)) {
            balanceMap = balanceCache.get(accountingPeriod);
        } else {
            balanceMap = new HashMap<Account, Money>();
            balanceCache.put(accountingPeriod, balanceMap);
        }
        balanceMap.put(account, balance);
        
        return balance;
    }
    
    @Override
    public synchronized <Entry extends JPABase> Entry loggedSave(User user) {
        check();

        return super.loggedSave(user);
    }

    @Override
    public synchronized <Entry extends JPABase> Entry save() {
        check();
        
        return super.save();
    }
    
    private void check() {
        if(id != null || getUpdated() != null) {
            throw new RuntimeException("Not allowed to update entries!");
        }
            
        // calculate checksum
        checksum = calculateChecksum();
    }
}
