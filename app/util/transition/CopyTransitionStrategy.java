package util.transition;

import models.Report;
import models.ReportItem;
import models.ReportTransition;

public class CopyTransitionStrategy implements TransitionStrategy {
    public Report transition(ReportTransition reportTransition, Report report) {
        Report resultReport = new Report();
        resultReport.order = report.order;
        resultReport.rebatePercentage = report.rebatePercentage;
        resultReport.conditions = report.conditions;

        resultReport.reportType = reportTransition.targetReportType;
        resultReport.save();

        for(ReportItem reportItem : report.reportItems) {
            ReportItem clonedReportItem = reportItem.duplicate();
            clonedReportItem.report = resultReport;
            clonedReportItem.save();
        }

        report.order.currentReport = resultReport;
        report.order.save();

        return resultReport;
    }
}
