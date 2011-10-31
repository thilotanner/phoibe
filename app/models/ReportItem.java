package models;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.FIELD)
public abstract class ReportItem extends EnhancedModel implements Cloneable {

    @ManyToOne
    public Report report;

    public abstract double getAmount();

    public abstract String getDescription();

    public abstract Money getPrice();

    public abstract ReportItem duplicate();
}
