package models;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPABase;
import play.i18n.Messages;
import util.hashing.HashingUtils;
import util.string.NonEmptyStringBuilder;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
