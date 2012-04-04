package models;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.GenericModel;
import play.i18n.Messages;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

@Entity
public class CreditorPayment extends EnhancedModel {

    @ManyToOne
    public Creditor creditor;

    @Valid
    @Embedded
    public Money amount;

    @Required
    @Temporal(TemporalType.DATE)
    public Date paid;

    @ManyToOne
    public Entry paymentEntry;

    @Transient
    public Account paymentAccount;

    public void buildEntry() {
        paymentEntry = new Entry();
        paymentEntry.date = new Date();
        paymentEntry.debit = Account.getCreditorAccount();
        paymentEntry.credit = paymentAccount;
        paymentEntry.accountingPeriod = AccountingPeriod.getActiveAccountingPeriod();
        paymentEntry.amount = amount;
        paymentEntry.description = String.format("%s: %s - %s", Messages.get("creditorPayment"), creditor.supplier.getLabel(), creditor.reference);
    }
}
