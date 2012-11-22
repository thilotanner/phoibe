package models;

import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class InputTaxJournal {

    private Account inputTaxAccount;

    private List<Creditor> creditors = new ArrayList<Creditor>();

    private Money totalNetAmountCreditors = new Money(CurrencyProvider.getDefaultCurrency());

    private Money totalValueAddedTaxCreditors = new Money(CurrencyProvider.getDefaultCurrency());

    private List<Creditor> correctionCreditors = new ArrayList<Creditor>();

    private Money totalNetAmountCorrectionCreditors = new Money(CurrencyProvider.getDefaultCurrency());

    private Money totalValueAddedTaxCorrectionCreditors = new Money(CurrencyProvider.getDefaultCurrency());

    public InputTaxJournal(Account inputTaxAccount) {
        this.inputTaxAccount = inputTaxAccount;
    }

    public Account getInputTaxAccount() {
        return inputTaxAccount;
    }

    public List<Creditor> getCreditors() {
        return creditors;
    }

    public void addCreditor(Creditor creditor) {
        creditors.add(creditor);

        totalNetAmountCreditors = totalNetAmountCreditors.add(creditor.creditorEntry.amount);

        totalValueAddedTaxCreditors = totalValueAddedTaxCreditors.add(creditor.valueAddedTaxEntry.amount);
    }

    public List<Creditor> getCorrectionCreditors() {
        return correctionCreditors;
    }

    public void addCorrectionCreditor(Creditor correctionCreditor) {
        correctionCreditors.add(correctionCreditor);

        totalNetAmountCorrectionCreditors = totalNetAmountCorrectionCreditors.add(correctionCreditor.amountDueEntry.amount);

        totalValueAddedTaxCorrectionCreditors = totalValueAddedTaxCorrectionCreditors.add(correctionCreditor.valueAddedTaxCorrectionEntry.amount);
    }

    public Money getTotalNetAmountCorrectionCreditors() {
        return totalNetAmountCorrectionCreditors;
    }

    public Money getTotalNetAmountCreditors() {
        return totalNetAmountCreditors;
    }

    public Money getTotalValueAddedTaxCorrectionCreditors() {
        return totalValueAddedTaxCorrectionCreditors;
    }

    public Money getTotalValueAddedTaxCreditors() {
        return totalValueAddedTaxCreditors;
    }

    public Money getTotalNetAmountSumCreditors() {
        return getTotalNetAmountCreditors().subtract(getTotalNetAmountCorrectionCreditors());
    }

    public Money getTotalValueAddedTaxSumCreditors() {
        return getTotalValueAddedTaxCreditors().subtract(getTotalValueAddedTaxCorrectionCreditors());
    }
}
