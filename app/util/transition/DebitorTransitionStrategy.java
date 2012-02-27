package util.transition;

import models.Debitor;
import models.Report;
import models.ReportTransition;

public class DebitorTransitionStrategy extends CopyTransitionStrategy {
    @Override
    public Report transition(ReportTransition reportTransition, Report report) {
        
        Debitor.buildAndSaveDebitor(report);
        
        return super.transition(reportTransition, report);
    }
}
