package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class BatchJobRun extends Model {

    public static boolean hasUnacknowledgedFailures() {
        return count("batchJobRunStatus = ? AND acknowledged = ?", BatchJobRunStatus.FAILED, false) > 0l;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    public Date endDate;

    public String jobClassName;

    @Lob
    public String log;

    @Enumerated(EnumType.STRING)
    public BatchJobRunStatus batchJobRunStatus;

    @Lob
    public String exceptionTitle;

    @Lob
    public String exception;

    public boolean acknowledged;

    public boolean isUnacknowledged() {
        return batchJobRunStatus.equals(BatchJobRunStatus.FAILED) && !acknowledged;
    }
}
