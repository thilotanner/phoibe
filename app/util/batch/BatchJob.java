package util.batch;

import models.BatchJobRun;
import models.BatchJobRunStatus;
import org.apache.commons.lang.exception.ExceptionUtils;
import play.jobs.Job;

import java.util.Date;

public class BatchJob extends Job {

    private static final String PLAY_EXCEPTION_PREFIX = "play.exceptions.JavaExecutionException";
    private static final String COLON_SPACE = ": ";

    private BatchJobRun batchJobRun;
    private BatchJobLogger batchJobLogger;

    @Override
    public void doJob() throws Exception
    {
        prepareJobRun();
        doBatchJob(this.batchJobLogger);
        finalizeJobRun(BatchJobRunStatus.SUCCESS);
    }

    @Override
    public Object doJobWithResult() throws Exception
    {
        prepareJobRun();
        Object result = doBatchJobWithResult(this.batchJobLogger);
        finalizeJobRun(BatchJobRunStatus.SUCCESS);
        return result;
    }

    private void prepareJobRun()
    {
        batchJobRun = new BatchJobRun();
        batchJobRun.batchJobRunStatus = BatchJobRunStatus.RUNNING;
        batchJobRun.jobClassName = this.getClass().getName();
        batchJobRun.startDate = new Date();
        batchJobLogger = new BatchJobLogger();
        BatchManager.getInstance().addRunningBatchJob(this);
    }

    private void finalizeJobRun(BatchJobRunStatus batchJobRunStatus)
    {
        BatchManager.getInstance().removeRunningBatchJob(this);

        batchJobRun.batchJobRunStatus = batchJobRunStatus;
        batchJobRun.endDate = new Date();
        batchJobRun.log = batchJobLogger.toString();

        BatchJobRunPersistingJob batchJobRunPersistingJob = new BatchJobRunPersistingJob(batchJobRun);
        batchJobRunPersistingJob.now();
    }

    @Override
    public void onException(Throwable e)
    {
        super.onException(e);

        batchJobRun.exceptionTitle = beautifyTitle(e.toString());
        batchJobRun.exception = ExceptionUtils.getFullStackTrace(e);

        finalizeJobRun(BatchJobRunStatus.FAILED);
    }

    public void doBatchJob(BatchJobLogger batchJobLogger) throws Exception {
        // Here you do the job
    }

    public Object doBatchJobWithResult(BatchJobLogger batchJobLogger) throws Exception {
        doBatchJob(batchJobLogger);
        return null;
    }

    public BatchJobRun getBatchJobRun() {
        return batchJobRun;
    }

    public BatchJobLogger getBatchJobLogger() {
        return batchJobLogger;
    }

    private String beautifyTitle(String title) {
        if(title.startsWith(PLAY_EXCEPTION_PREFIX)) {
            title = title.substring(PLAY_EXCEPTION_PREFIX.length());
        }

        if(title.startsWith(COLON_SPACE)) {
            title = title.substring(COLON_SPACE.length());
        }

        return title;
    }
}
