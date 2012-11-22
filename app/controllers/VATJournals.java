package controllers;

import models.Creditor;
import models.Debitor;
import models.VATJournal;
import play.data.validation.Valid;

import java.util.List;

public class VATJournals extends ApplicationController {
    public static void index() {
        render();
    }

    public static void journal(@Valid VATJournal journal) {
        fillJournal(journal);

        render(journal);
    }

    private static void fillJournal(VATJournal journal) {
        calculateDebitors(journal);

        calculateCorrectionDebitors(journal);

        calculateCreditors(journal);

        calculateCorrectionCreditors(journal);
    }

    private static void calculateDebitors(VATJournal journal) {
        List<Debitor> debitors = Debitor.find("debitorEntry.date >= ? and debitorEntry.date <= ?", journal.from, journal.to).fetch();

        journal.setDebitors(debitors);
    }

    private static void calculateCorrectionDebitors(VATJournal journal) {
        List<Debitor> correctionDebitors =
                Debitor.find("valueAddedTaxCorrectionEntry.date >= ? and valueAddedTaxCorrectionEntry.date <= ?", journal.from, journal.to).fetch();

        journal.setCorrectionDebitors(correctionDebitors);
    }

    private static void calculateCreditors(VATJournal journal) {
        List<Creditor> creditors =
                Creditor.find("dateOfInvoice >= ? and dateOfInvoice <= ? and valueAddedTaxRate != null", journal.from, journal.to).fetch();

        for(Creditor creditor : creditors) {
            journal.addCreditor(creditor);
        }
    }

    private static void calculateCorrectionCreditors(VATJournal journal) {
        List<Creditor> correctionCreditors =
                Creditor.find("valueAddedTaxCorrectionEntry.date >= ? and valueAddedTaxCorrectionEntry.date <= ? and valueAddedTaxRate != null", journal.from, journal.to).fetch();

        for(Creditor correctionCreditor : correctionCreditors) {
            journal.addCorrectionCreditor(correctionCreditor);
        }
    }
}
