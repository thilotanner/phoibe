package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
public class Debitor extends EnhancedModel {

    public static List<Debitor> getOverdueDebitors() {
        return Debitor.find("debitorStatus = 'DUE' AND due < ?", new Date()).fetch();
    }
    
    @ManyToOne
    public Report report;

    @Temporal(TemporalType.DATE)
    public Date due;

    @Enumerated(EnumType.STRING)
    public DebitorStatus debitorStatus;

    @OneToMany(mappedBy = "debitor")
    public List<DebitorPaymentReceipt> debitorPaymentReceipts;
    
    public Money getAmountDue() {
        Money amountDue = new Money(report.getTaxedTotalPrice());
        for(DebitorPaymentReceipt debitorPaymentReceipt : debitorPaymentReceipts) {
            amountDue.subtract(debitorPaymentReceipt.amount);
        }
        return amountDue;
    }

    public boolean isOverdue() {
        return debitorStatus == DebitorStatus.DUE && due.before(new Date());
    }
}
