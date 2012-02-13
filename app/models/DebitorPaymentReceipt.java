package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
}
