package models;

import play.db.jpa.Model;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

public class Report extends Model {

    @ManyToOne
    public ReportType reportType;

    @ManyToOne
    public Order order;

    @OneToMany(mappedBy = "report")
    public List<ReportItem> reportItems;
}
