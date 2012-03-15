package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "Orders")
public class Order extends EnhancedModel {

    @Required
    public String description;

    @Lob
    public String comments;

    @Required
    @ManyToOne
    public Contact orderingContact;

    @Required
    @ManyToOne
    public Contact shippingContact;

    @Required
    @ManyToOne
    public Contact billingContact;

    @OneToMany(mappedBy = "order")
    public List<Report> reports;

    @ManyToOne
    public Report currentReport;

    @Enumerated(EnumType.STRING)
    public OrderStatus orderStatus;

    public boolean isEditable() {
        return orderStatus == OrderStatus.NEW || orderStatus == OrderStatus.IN_PROGRESS;
    }
}
