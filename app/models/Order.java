package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Orders")
public class Order extends EnhancedModel {

    @Required
    public String description;

    @Lob
    public String comments;

    @ManyToOne
    public Contact orderingContact;

    @ManyToOne
    public Contact shippingContact;

    @ManyToOne
    public Contact billingContact;
}
