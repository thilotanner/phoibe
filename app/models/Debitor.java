package models;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Debitor extends EnhancedModel {

    @ManyToOne
    public Report report;

    @Embedded
    public Money amountPaid;
}
