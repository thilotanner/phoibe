package models;

import play.Play;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Creditor extends EnhancedModel {

    public static List<Creditor> getOverdueCreditors() {
        return Creditor.find("creditorStatus = 'DUE' AND due < ?", new Date()).fetch();
    }

    @Required
    @ManyToOne
    public Contact supplier;

    public String reference;

    @Valid
    @Embedded
    public Money amount;

    @ManyToOne
    public Account expenseAccount;

    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;

    @ManyToOne
    public Account inputTaxAccount;

    @Required
    @Temporal(TemporalType.DATE)
    public Date due;

    @Enumerated(EnumType.STRING)
    public CreditorStatus creditorStatus;

    @OneToMany(mappedBy = "creditor")
    public List<CreditorPayment> creditorPayments;

    @ManyToOne
    public Entry creditorEntry;
    
    @ManyToOne
    public Entry valueAddedTaxEntry;
    
    @ManyToOne
    public Entry amountDueEntry;

    @ManyToOne
    public Entry valueAddedTaxCorrectionEntry;

    public Money getAmountPaid() {
        Money sum = new Money(CurrencyProvider.getDefaultCurrency());
        if(creditorPayments != null) {
            for(CreditorPayment creditorPayment : creditorPayments) {
                sum = sum.add(creditorPayment.amount);
            }
        }
        return sum;
    }
    
    public Money getAmountDue() {
        // total price
        Money amountDue = new Money(amount);

        // minus payments
        amountDue = amountDue.subtract(getAmountPaid());

        // minus amount due entry (discount)
        if(amountDueEntry != null && amountDueEntry.amount != null) {
            amountDue = amountDue.subtract(amountDueEntry.amount);
        }
        if(valueAddedTaxCorrectionEntry != null && valueAddedTaxCorrectionEntry.amount != null) {
            amountDue = amountDue.subtract(valueAddedTaxCorrectionEntry.amount);
        }

        return amountDue;
    }
    
    public boolean isOverdue() {
        return creditorStatus == CreditorStatus.DUE && due.before(new Date());
    }

    public void buildAndSaveCreditorEntries() {
        Money netAmount;
        if(valueAddedTaxRate != null) {
            // with value added text
            netAmount = getAmountDue().divide(BigDecimal.ONE.add(valueAddedTaxRate.getRateFactor()));
        } else {
            // without value added text
            netAmount = getAmountDue();
        }
       
        creditorEntry = new Entry();
        creditorEntry.date = new Date();
        creditorEntry.amount = new Money(netAmount);
        creditorEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        creditorEntry.debit = expenseAccount;
        creditorEntry.credit = Account.getCreditorAccount();
        creditorEntry.voucher = reference;
        creditorEntry.description = String.format("%s: %s - %s", Messages.get("creditor"), supplier.getLabel(), reference);
        creditorEntry.save();

        if(valueAddedTaxRate != null) {
            valueAddedTaxEntry = new Entry();
            valueAddedTaxEntry.debit = inputTaxAccount;
            valueAddedTaxEntry.credit = Account.getCreditorAccount();
            valueAddedTaxEntry.amount = this.amount.subtract(creditorEntry.amount);
            valueAddedTaxEntry.date = new Date();
            valueAddedTaxEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
            valueAddedTaxEntry.voucher = reference;
            valueAddedTaxEntry.description = String.format("%s %s: %s - %s", Messages.get("valueAddedTax"), Messages.get("creditor"), supplier.getLabel(), reference);
            valueAddedTaxEntry.save();
        }
    }

    public void buildAndSaveDiscountEntries() {
        Money netAmount;
        if(valueAddedTaxRate != null) {
            // with value added text
            netAmount = getAmountDue().divide(BigDecimal.ONE.add(valueAddedTaxRate.getRateFactor()));
        } else {
            // without value added text
            netAmount = getAmountDue();
        }

        amountDueEntry = new Entry();
        amountDueEntry.date = new Date();
        amountDueEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        amountDueEntry.debit = Account.getCreditorAccount();
        amountDueEntry.credit = Account.getPurchaseDiscountAccount();
        amountDueEntry.amount = new Money(netAmount);
        amountDueEntry.description = String.format("%s: %s - %s", Messages.get("creditor.discount"), supplier.getLabel(), reference);
        amountDueEntry.save();

        if(valueAddedTaxRate != null) {
            valueAddedTaxCorrectionEntry = new Entry();
            valueAddedTaxCorrectionEntry.date = new Date();
            valueAddedTaxCorrectionEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
            valueAddedTaxCorrectionEntry.debit = Account.getCreditorAccount();
            valueAddedTaxCorrectionEntry.credit = inputTaxAccount;
            valueAddedTaxCorrectionEntry.amount = this.amount.subtract(getAmountPaid()).subtract(amountDueEntry.amount);
            valueAddedTaxCorrectionEntry.description = String.format("%s %s: %s - %s", Messages.get("valueAddedTax"), Messages.get("correction"), supplier.getLabel(), reference);
            valueAddedTaxCorrectionEntry.save();
        }
    }

    public void close() {
        // close creditor
        creditorStatus = CreditorStatus.PAID;
        this.save();
    }

    public boolean isEditable() {
        return creditorStatus == CreditorStatus.DUE;
    }

    public File getAttachmentFolder() {
        return Play.getFile("data" +
                File.separator +
                "attachments" +
                File.separator +
                "creditors" +
                File.separator +
                this.id +
                File.separator);
    }

    public List<CreditorAttachment> getAttachments() {
        List<CreditorAttachment> creditorAttachments = new ArrayList<CreditorAttachment>();

        if (getAttachmentFolder().exists()) {
            for(File file : getAttachmentFolder().listFiles()) {
                creditorAttachments.add(new CreditorAttachment(file));
            }
        }

        return creditorAttachments;
    }
}
