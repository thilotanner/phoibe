package models;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OpeningBalance extends EnhancedModel {

    @ManyToOne
    public Account account;

    @ManyToOne
    public AccountingPeriod accountingPeriod;

    @Embedded
    public Money openingBalance;

}
