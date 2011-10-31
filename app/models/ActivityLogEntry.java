package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ActivityLogEntry extends EnhancedModel {
    @Temporal(TemporalType.TIMESTAMP)
    public Date date;

    @Lob
    public String message;

    @Enumerated(EnumType.STRING)
    public ActivityLogAction activityLogAction;

    @ManyToOne
    public User user;
}
