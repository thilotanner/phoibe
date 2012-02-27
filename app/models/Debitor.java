package models;

import play.i18n.Messages;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Debitor extends EnhancedModel {

    public static List<Debitor> getOverdueDebitors() {
        return Debitor.find("debitorStatus = 'DUE' AND due < ?", new Date()).fetch();
    }
    
    public static Debitor buildAndSaveDebitor(Report report) {

        // create debitor entry
        Entry debitorEntry = new Entry();
        debitorEntry.debit = Account.getDebitorAccount();
        debitorEntry.credit = Account.getRevenueAccount();
        debitorEntry.amount = report.getTaxedTotalPrice();
        debitorEntry.date = new Date();
        debitorEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        debitorEntry.voucher = report.id.toString();
        debitorEntry.description = String.format("%s: %s", Messages.get("debitor"), report.getLabel());
        debitorEntry.save();

        // create debitor
        Debitor debitor = new Debitor();
        debitor.report = report;
        debitor.debitorStatus = DebitorStatus.DUE;
        debitor.debitorEntry = debitorEntry;

        // due in 30 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        debitor.due = calendar.getTime();
        
        debitor.save();
        
        return debitor;
    }
    
    @ManyToOne
    public Report report;

    @Temporal(TemporalType.DATE)
    public Date due;

    @Enumerated(EnumType.STRING)
    public DebitorStatus debitorStatus;

    @OneToMany(mappedBy = "debitor")
    public List<DebitorPaymentReceipt> debitorPaymentReceipts;

    @ManyToOne
    public Entry debitorEntry;

    @ManyToOne
    public Entry amountDueEntry;
    
    public Money getAmountDue() {
        // total price
        Money amountDue = new Money(report.getTaxedTotalPrice());

        // minus payment receipts
        for(DebitorPaymentReceipt debitorPaymentReceipt : debitorPaymentReceipts) {
            amountDue = amountDue.subtract(debitorPaymentReceipt.amount);
        }

        // minus amount due entry (discount or charge off)
        if(amountDueEntry != null) {
            amountDue = amountDue.subtract(amountDueEntry.amount);
        }
        return amountDue;
    }

    public boolean isOverdue() {
        return debitorStatus == DebitorStatus.DUE && due.before(new Date());
    }

    public Entry buildDiscountEntry() {
        Entry entry = new Entry();
        entry.date = new Date();
        entry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        entry.debit = Account.getDiscountAccount();
        entry.credit = Account.getDebitorAccount();
        entry.amount = getAmountDue();
        entry.description = String.format("%s: %s", Messages.get("debitor.discount"), report.getLabel());
        return entry;
    }

    public Entry buildChargeOffEntry() {
        Entry entry = new Entry();
        entry.date = new Date();
        entry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        entry.debit = Account.getChargeOffAccount();
        entry.credit = Account.getDebitorAccount();
        entry.amount = getAmountDue();
        entry.description = String.format("%s: %s", Messages.get("debitor.chargeOff"), report.getLabel());
        return entry;
    }

    public void close(Entry amountDueEntry) {
        // finish order
        report.order.orderStatus = OrderStatus.FINISHED;
        report.order.save();

        // close debitor
        debitorStatus = DebitorStatus.PAID;
        this.amountDueEntry = amountDueEntry;
        save();
    }
}
