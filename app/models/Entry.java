package models;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Entry extends Model {

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
}
