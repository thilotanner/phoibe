package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Account extends EnhancedModel {
    public String number;

    public String description;

    @Enumerated(EnumType.STRING)
    public AccountType accountType;

    @OneToMany(mappedBy = "debit")
    public List<Entry> debitEntries;

    @OneToMany(mappedBy = "credit")
    public List<Entry> creditEntries;

    public String getLabel() {
        return String.format("%s %s", number, description);
    }
}
