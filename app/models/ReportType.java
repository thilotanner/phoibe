package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ReportType extends EnhancedModel {

    @Required
    public String name;

    public boolean rootReportType;

    @Enumerated(EnumType.STRING)
    public ReportColor reportColor;

    @Required
    @Lob
    public String template;

    @OneToMany(mappedBy = "reportType")
    public List<ReportTransition> reportTransitions;
}
