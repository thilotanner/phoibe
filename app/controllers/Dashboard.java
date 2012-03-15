package controllers;

import models.BatchJobRun;
import models.Creditor;
import models.Debitor;
import play.i18n.Messages;

import java.util.List;

public class Dashboard extends ApplicationController {
    public static void index() {
        if(BatchJobRun.hasUnacknowledgedFailures()) {
            flash.now("error", Messages.get("batchJobRun.unacknowledgedFailuresExist"));
        }

        List<Debitor> overdueDebitors = Debitor.getOverdueDebitors();
        
        List<Creditor> overdueCreditors = Creditor.getOverdueCreditors();
        
        render(overdueDebitors, overdueCreditors);
    }
}
