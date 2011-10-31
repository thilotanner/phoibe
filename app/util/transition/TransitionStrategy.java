package util.transition;

import models.Report;
import models.ReportTransition;

public interface TransitionStrategy {
    public Report transition(ReportTransition reportTransition, Report report);
}
