package models;

import java.util.Date;
import java.util.List;

public class VATJournal {

    public Date from;

    public Date to;

    public List<Debitor> debitors;

    public Money totalNetAmountDebitors;

    public Money totalValueAddedTaxDebitors;

    public List<Debitor> correctionDebitors;

    public Money totalNetAmountCorrectionDebitors;

    public Money totalValueAddedTaxCorrectionDebitors;

    public List<Creditor> creditors;

    public Money totalNetAmountCreditors;

    public Money totalValueAddedTaxCreditors;

    public List<Creditor> correctionCreditors;

    public Money totalNetAmountCorrectionCreditors;

    public Money totalValueAddedTaxCorrectionCreditors;

    public Money totalNetAmount;

    public Money totalValueAddedTax;
}
