package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ReportTransition extends EnhancedModel {

    @ManyToOne
    public ReportType reportType;

    @ManyToOne
    public ReportType targetReportType;

    public String transitionStrategyClassName;
}
