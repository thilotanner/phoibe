package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Creditor extends EnhancedModel {

    @ManyToOne
    public Contact supplier;

    public String reference;
}
