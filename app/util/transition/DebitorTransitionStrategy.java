package util.transition;

import models.Debitor;
import models.DebitorStatus;
import models.Report;
import models.ReportTransition;

import java.util.Calendar;

public class DebitorTransitionStrategy extends CopyTransitionStrategy {
    @Override
    public Report transition(ReportTransition reportTransition, Report report) {

        // create debitor
        Debitor debitor = new Debitor();
        debitor.report = report;
        debitor.debitorStatus = DebitorStatus.DUE;

        // due in 30 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        debitor.due = calendar.getTime();

        debitor.save();

        return super.transition(reportTransition, report);
    }
}
