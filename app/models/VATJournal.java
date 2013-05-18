package models;

import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VATJournal {

    public Date from;

    public Date to;

    private List<Debitor> debitors = new ArrayList<Debitor>();

    private Money totalNetAmountDebitors = new Money(CurrencyProvider.getDefaultCurrency());

    private Money totalValueAddedTaxDebitors = new Money(CurrencyProvider.getDefaultCurrency());

    private List<Debitor> correctionDebitors = new ArrayList<Debitor>();

    private Money totalNetAmountCorrectionDebitors = new Money(CurrencyProvider.getDefaultCurrency());

    private Money totalValueAddedTaxCorrectionDebitors = new Money(CurrencyProvider.getDefaultCurrency());

    private Map<Account, InputTaxJournal> inputTaxJournals = new HashMap<Account, InputTaxJournal>();

    public List<Debitor> getCorrectionDebitors() {
        return correctionDebitors;
    }

    public void setCorrectionDebitors(List<Debitor> correctionDebitors) {
        for(Debitor debitor : correctionDebitors) {
            addCorrectionDebitor(debitor);
        }
    }

    public void addCorrectionDebitor(Debitor debitor) {
        correctionDebitors.add(debitor);

        totalNetAmountCorrectionDebitors = totalNetAmountCorrectionDebitors.add(debitor.amountDueEntry.amount);

        totalValueAddedTaxCorrectionDebitors = totalValueAddedTaxCorrectionDebitors.add(debitor.valueAddedTaxCorrectionEntry.amount);
    }

    public List<Debitor> getDebitors() {
        return debitors;
    }

    public void setDebitors(List<Debitor> debitors) {
        for(Debitor debitor : debitors) {
            addDebitor(debitor);
        }
    }

    public void addDebitor(Debitor debitor) {
        debitors.add(debitor);

        totalNetAmountDebitors = totalNetAmountDebitors.add(debitor.debitorEntry.amount);

        totalValueAddedTaxDebitors = totalValueAddedTaxDebitors.add(debitor.valueAddedTaxEntry.amount);
    }

    public Money getTotalNetAmountCorrectionDebitors() {
        return totalNetAmountCorrectionDebitors;
    }

    public Money getTotalNetAmountDebitors() {
        return totalNetAmountDebitors;
    }

    public Money getTotalValueAddedTaxCorrectionDebitors() {
        return totalValueAddedTaxCorrectionDebitors;
    }

    public Money getTotalValueAddedTaxDebitors() {
        return totalValueAddedTaxDebitors;
    }

    public void addCreditor(Creditor creditor) {
        Account inputTaxAccount = creditor.valueAddedTaxEntry.debit;

        if(!inputTaxJournals.containsKey(inputTaxAccount)) {
            inputTaxJournals.put(inputTaxAccount, new InputTaxJournal(inputTaxAccount));
        }

        inputTaxJournals.get(inputTaxAccount).addCreditor(creditor);
    }

    public void addCorrectionCreditor(Creditor correctionCreditor) {
        Account inputTaxAccount = correctionCreditor.valueAddedTaxEntry.debit;

        if(!inputTaxJournals.containsKey(inputTaxAccount)) {
            inputTaxJournals.put(inputTaxAccount, new InputTaxJournal(inputTaxAccount));
        }

        inputTaxJournals.get(inputTaxAccount).addCorrectionCreditor(correctionCreditor);
    }

    public List<InputTaxJournal> getInputTaxJournals() {
        return new ArrayList<InputTaxJournal>(inputTaxJournals.values());
    }

    public Money getTotalNetAmountSumDebitors() {
        return getTotalNetAmountDebitors().subtract(getTotalNetAmountCorrectionDebitors());
    }

    public Money getTotalValueAddedTaxSumDebitors() {
        return getTotalValueAddedTaxDebitors().subtract(getTotalValueAddedTaxCorrectionDebitors());
    }

    public Money getTotalNetSumInputTax() {
        Money totalNetSumInputTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(InputTaxJournal inputTaxJournal : inputTaxJournals.values()) {
            totalNetSumInputTax = totalNetSumInputTax.add(inputTaxJournal.getTotalNetAmountSumCreditors());
        }
        return totalNetSumInputTax;
    }

    public Money getTotalValueAddedTaxInputTax() {
        Money totalValueAddedTaxInputTax = new Money(CurrencyProvider.getDefaultCurrency());
        for(InputTaxJournal inputTaxJournal : inputTaxJournals.values()) {
            totalValueAddedTaxInputTax = totalValueAddedTaxInputTax.add(inputTaxJournal.getTotalValueAddedTaxSumCreditors());
        }
        return totalValueAddedTaxInputTax;
    }

    public Money getTotalNetAmount() {
        return getTotalNetAmountSumDebitors().subtract(getTotalNetSumInputTax());
    }

    public Money getTotalValueAddedTax() {
        return getTotalValueAddedTaxSumDebitors().subtract(getTotalValueAddedTaxInputTax());
    }
}
