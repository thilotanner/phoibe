package models;

import play.Play;
import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
public class Order extends EnhancedModel {

    private static final NumberFormat REFERENCE_NUMBER_FORMAT = new DecimalFormat("000000");
    
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

    @Required
    @ManyToOne
    public AccountingPeriod accountingPeriod;

    @OneToMany(mappedBy = "order")
    public List<Report> reports;

    @ManyToOne
    public Report currentReport;

    @Enumerated(EnumType.STRING)
    public OrderStatus orderStatus;
    
    public boolean isEditable() {
        return orderStatus == OrderStatus.NEW || orderStatus == OrderStatus.IN_PROGRESS;
    }

    public boolean isDeletable() {
        return reports == null || reports.size() == 0;
    }
    
    public String getReferenceNumber() {
        // count all orders of the same accounting period with lower id
        Long count = Order.count("accountingPeriod = ? AND id < ?", accountingPeriod, id);
        count++;  // next number
        return String.format("%s-%s", accountingPeriod.description, REFERENCE_NUMBER_FORMAT.format(count));
    }
    
    public File getAttachmentFolder() {
        return Play.getFile("data" + 
                File.separator + 
                "attachments" + 
                File.separator + 
                "orders" + 
                File.separator +
                this.id +
                File.separator);
        
    }
    
    public List<OrderAttachment> getAttachments() {
        List<OrderAttachment> orderAttachments = new ArrayList<OrderAttachment>();

        if (getAttachmentFolder().exists()) {
            for(File file : getAttachmentFolder().listFiles()) {
                orderAttachments.add(new OrderAttachment(file));
            }
        }

        return orderAttachments;
    }
}
