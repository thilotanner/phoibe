package controllers;

import models.Creditor;
import models.Debitor;
import models.Money;
import models.VATJournal;
import play.data.validation.Valid;
import util.i18n.CurrencyProvider;

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

        Money totalNetAmount = journal.totalNetAmountDebitors;
        totalNetAmount = totalNetAmount.subtract(journal.totalNetAmountCorrectionDebitors);
        totalNetAmount = totalNetAmount.subtract(journal.totalNetAmountCreditors);
        totalNetAmount = totalNetAmount.subtract(journal.totalNetAmountCorrectionCreditors);
        journal.totalNetAmount = totalNetAmount;

        Money totalValueAddedTax = journal.totalValueAddedTaxDebitors;
        totalValueAddedTax = totalValueAddedTax.subtract(journal.totalValueAddedTaxCorrectionDebitors);
        totalValueAddedTax = totalValueAddedTax.subtract(journal.totalValueAddedTaxCreditors);
        totalValueAddedTax = totalValueAddedTax.subtract(journal.totalValueAddedTaxCorrectionCreditors);
        journal.totalValueAddedTax = totalValueAddedTax;
    }

    private static void calculateDebitors(VATJournal journal) {
        journal.debitors =
                Debitor.find("debitorEntry.date >= ? and debitorEntry.date <= ?", journal.from, journal.to).fetch();

        Money totalNetAmount = new Money(CurrencyProvider.getDefaultCurrency());
        for(Debitor debitor : journal.debitors) {
            totalNetAmount = totalNetAmount.add(debitor.debitorEntry.amount);
        }
        journal.totalNetAmountDebitors = totalNetAmount;

        Money totalValueAddedTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(Debitor debitor : journal.debitors) {
            totalValueAddedTax = totalValueAddedTax.add(debitor.valueAddedTaxEntry.amount);
        }
        journal.totalValueAddedTaxDebitors = totalValueAddedTax;

    }

    private static void calculateCorrectionDebitors(VATJournal journal) {
        journal.correctionDebitors =
                Debitor.find("valueAddedTaxCorrectionEntry.date >= ? and valueAddedTaxCorrectionEntry.date <= ?", journal.from, journal.to).fetch();

        Money totalNetAmount = new Money(CurrencyProvider.getDefaultCurrency());
        for(Debitor debitor : journal.correctionDebitors) {
            totalNetAmount = totalNetAmount.add(debitor.amountDueEntry.amount);
        }
        journal.totalNetAmountCorrectionDebitors = totalNetAmount;

        Money totalValueAddedTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(Debitor debitor : journal.correctionDebitors) {
            totalValueAddedTax = totalValueAddedTax.add(debitor.valueAddedTaxCorrectionEntry.amount);
        }
        journal.totalValueAddedTaxCorrectionDebitors = totalValueAddedTax;
    }

    private static void calculateCreditors(VATJournal journal) {
        journal.creditors =
                Creditor.find("dateOfInvoice >= ? and dateOfInvoice <= ? and valueAddedTaxRate != null", journal.from, journal.to).fetch();

        Money totalNetAmount = new Money(CurrencyProvider.getDefaultCurrency());
        for(Creditor creditor : journal.creditors) {
            totalNetAmount = totalNetAmount.add(creditor.creditorEntry.amount);
        }
        journal.totalNetAmountCreditors = totalNetAmount;

        Money totalValueAddedTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(Creditor creditor : journal.creditors) {
            totalValueAddedTax = totalValueAddedTax.add(creditor.valueAddedTaxEntry.amount);
        }
        journal.totalValueAddedTaxCreditors = totalValueAddedTax;
    }

    private static void calculateCorrectionCreditors(VATJournal journal) {
        journal.correctionCreditors =
                Creditor.find("valueAddedTaxCorrectionEntry.date >= ? and valueAddedTaxCorrectionEntry.date <= ? and valueAddedTaxRate != null", journal.from, journal.to).fetch();

        Money totalNetAmount = new Money(CurrencyProvider.getDefaultCurrency());
        for(Creditor creditor : journal.correctionCreditors) {
            totalNetAmount = totalNetAmount.add(creditor.amountDueEntry.amount);
        }
        journal.totalNetAmountCorrectionCreditors = totalNetAmount;

        Money totalValueAddedTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(Creditor creditor : journal.correctionCreditors) {
            totalValueAddedTax = totalValueAddedTax.add(creditor.valueAddedTaxCorrectionEntry.amount);
        }
        journal.totalValueAddedTaxCorrectionCreditors = totalValueAddedTax;
    }
}
