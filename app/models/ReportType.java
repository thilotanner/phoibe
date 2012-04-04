package models;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ReportType extends EnhancedModel {

    @Required
    public String name;

    public boolean rootReportType;

    public String reportColor;

    @Required
    @Lob
    public String template;

    @OneToMany(mappedBy = "reportType")
    public List<ReportTransition> reportTransitions;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("name", name)
                .append("rootReportType", rootReportType)
                .append("reportColor", reportColor)
                .toString();
    }
}
