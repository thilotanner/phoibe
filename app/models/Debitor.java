package models;

import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
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
        debitorEntry.amount = report.getTotalPrice();
        debitorEntry.date = new Date();
        debitorEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        debitorEntry.voucher = report.id.toString();
        debitorEntry.description = String.format("%s: %s", Messages.get("debitor"), report.getLabel());
        debitorEntry.save();

        // create value added tax entry
        Entry valueAddedTaxEntry = new Entry();
        valueAddedTaxEntry.debit = Account.getDebitorAccount();
        valueAddedTaxEntry.credit = Account.getValueAddedTaxAccount();
        valueAddedTaxEntry.amount = report.getTax();
        valueAddedTaxEntry.date = new Date();
        valueAddedTaxEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        valueAddedTaxEntry.voucher = report.id.toString();
        valueAddedTaxEntry.description = String.format("%s %s: %s", Messages.get("valueAddedTax"), Messages.get("debitor"), report.getLabel());
        valueAddedTaxEntry.save();

        // create debitor
        Debitor debitor = new Debitor();
        debitor.report = report;
        debitor.debitorStatus = DebitorStatus.DUE;
        debitor.debitorEntry = debitorEntry;
        debitor.valueAddedTaxEntry = valueAddedTaxEntry;

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
    public Entry valueAddedTaxEntry;

    @ManyToOne
    public Entry amountDueEntry;
    
    @ManyToOne
    public Entry valueAddedTaxCorrectionEntry;
    
    public Money getAmountPaid() {
        Money sum = new Money(CurrencyProvider.getDefaultCurrency());
        for(DebitorPaymentReceipt debitorPaymentReceipt : debitorPaymentReceipts) {
            sum = sum.add(debitorPaymentReceipt.amount);
        }
        return sum;
    }
    
    public Money getAmountDue() {
        // total price
        Money amountDue = new Money(report.getTaxedTotalPrice());

        // minus payment receipts
        amountDue = amountDue.subtract(getAmountPaid());

        // minus amount due entry (discount or charge off)
        if(amountDueEntry != null && amountDueEntry.amount != null) {
            amountDue = amountDue.subtract(amountDueEntry.amount);
            amountDue = amountDue.subtract(valueAddedTaxCorrectionEntry.amount);
        }

        return amountDue;
    }

    public boolean isOverdue() {
        return debitorStatus == DebitorStatus.DUE && due.before(new Date());
    }

    public void buildAndSaveDiscountEntries() {
        amountDueEntry = new Entry();
        amountDueEntry.date = new Date();
        amountDueEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        amountDueEntry.debit = Account.getDiscountAccount();
        amountDueEntry.credit = Account.getDebitorAccount();
        amountDueEntry.amount = getAmountDue().divide(BigDecimal.ONE.add(report.getValueAddedTaxToTotalPriceRatio()));
        amountDueEntry.description = String.format("%s: %s", Messages.get("debitor.discount"), report.getLabel());
        amountDueEntry.save();

        buildAndSaveVatCorrectionEntry();

        this.save();
    }

    public void buildAndSaveChargeOffEntries() {
        amountDueEntry = new Entry();
        amountDueEntry.date = new Date();
        amountDueEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        amountDueEntry.debit = Account.getChargeOffAccount();
        amountDueEntry.credit = Account.getDebitorAccount();
        amountDueEntry.amount = getAmountDue().divide(BigDecimal.ONE.add(report.getValueAddedTaxToTotalPriceRatio()));
        amountDueEntry.description = String.format("%s: %s", Messages.get("debitor.chargeOff"), report.getLabel());
        amountDueEntry.save();

        buildAndSaveVatCorrectionEntry();

        this.save();
    }

    public void close() {
        // finish order
        report.order.orderStatus = OrderStatus.FINISHED;
        report.order.save();

        // close debitor
        debitorStatus = DebitorStatus.PAID;
        save();
    }

    public boolean isEditable() {
        return debitorStatus == DebitorStatus.DUE;
    }

    private void buildAndSaveVatCorrectionEntry() {
        valueAddedTaxCorrectionEntry = new Entry();
        valueAddedTaxCorrectionEntry.date = new Date();
        valueAddedTaxCorrectionEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        valueAddedTaxCorrectionEntry.debit = Account.getValueAddedTaxAccount();
        valueAddedTaxCorrectionEntry.credit = Account.getDebitorAccount();
        valueAddedTaxCorrectionEntry.amount = report.getTaxedTotalPrice().subtract(getAmountPaid()).subtract(amountDueEntry.amount);
        valueAddedTaxCorrectionEntry.description = String.format("%s %s: %s", Messages.get("valueAddedTax"), Messages.get("correction"), report.getLabel());
        valueAddedTaxCorrectionEntry.save();
    }
}
