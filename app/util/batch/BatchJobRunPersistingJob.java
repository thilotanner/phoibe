package util.batch;

import models.BatchJobRun;
import play.jobs.Job;

public class BatchJobRunPersistingJob extends Job {

    private BatchJobRun batchJobRun;

    public BatchJobRunPersistingJob(BatchJobRun batchJobRun) {
        this.batchJobRun = batchJobRun;
    }

    @Override
    public void doJob() throws Exception {
        batchJobRun.save();
    }
}
