package models;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

@Entity
public class DebitorPaymentReceipt extends EnhancedModel {

    @ManyToOne
    public Debitor debitor;

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
        paymentEntry.debit = paymentAccount;
        paymentEntry.credit = Account.getDebitorAccount();
        paymentEntry.accountingPeriod = debitor.report.order.accountingPeriod;
        paymentEntry.amount = amount;
        paymentEntry.description = String.format("%s: %s", Messages.get("debitorPaymentReceipt"), debitor.report.getLabel());
    }
}
