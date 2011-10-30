package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Report extends EnhancedModel {

    @ManyToOne
    public ReportType reportType;

    @ManyToOne
    public Order order;

    @OneToMany(mappedBy = "report")
    public List<ReportItem> reportItems;
}
