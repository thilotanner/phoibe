package controllers;

import models.Creditor;
import models.Debitor;

import java.util.List;

public class Dashboard extends ApplicationController {
    public static void index() {
        List<Debitor> overdueDebitors = Debitor.getOverdueDebitors();
        
        List<Creditor> overdueCreditors = Creditor.getOverdueCreditors();
        
        render(overdueDebitors, overdueCreditors);
    }
}
