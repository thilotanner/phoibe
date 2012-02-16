package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
public class Creditor extends EnhancedModel {

    public static List<Creditor> getOverdueCreditors() {
        return Creditor.find("paid = null AND due < ?", new Date()).fetch();
    }

    @Required
    @ManyToOne
    public Contact supplier;

    public String reference;

    @Valid
    @Embedded
    public Money amount;

    @Temporal(TemporalType.DATE)
    public Date due;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="valuePaid")),
            @AttributeOverride(name="currencyCode", column = @Column(name="currencyCodePaid"))
    })
    public Money amountPaid;

    @Temporal(TemporalType.DATE)
    public Date paid;

    public CreditorStatus getCreditorStatus() {
        if(paid == null) {
            return CreditorStatus.DUE;
        }
        return CreditorStatus.PAID;
    }

    public boolean isOverdue() {
        return getCreditorStatus() == CreditorStatus.DUE && due.before(new Date());
    }
}
