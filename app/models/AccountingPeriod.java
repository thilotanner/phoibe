package models;

import play.data.validation.Required;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class AccountingPeriod extends EnhancedModel {

    public static AccountingPeriod getActiveAccountingPeriod() {
        AccountingPeriod accountingPeriod = AccountingPeriod.find("active = ?", true).first();

        if(accountingPeriod == null) {
            accountingPeriod = AccountingPeriod.all().first();
        }

        return accountingPeriod;
    }

    @Required
    public String description;

    public boolean active;

    @OneToMany(mappedBy = "accountingPeriod", cascade = CascadeType.ALL)
    public List<OpeningBalance> openingBalances;
}
