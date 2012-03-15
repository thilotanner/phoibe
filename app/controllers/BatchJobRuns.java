package controllers;

import models.BatchJobRun;
import util.batch.BatchJob;
import util.batch.BatchManager;

import java.util.List;

public class BatchJobRuns extends ApplicationController {

    public static void index(int page) {
        
        BatchManager batchManager = BatchManager.getInstance();
        List<BatchJob> batchJobs = batchManager.getRunningJobs();
        String status = batchManager.getStatus();

        if (page < 1) {
            page = 1;
        }

        List<BatchJobRun> batchJobRuns = 
                BatchJobRun.find("order by endDate DESC").from((page - 1) * getPageSize()).fetch(getPageSize());
        
        Long count = BatchJobRun.count();

        renderArgs.put("pageSize", getPageSize());
        render(batchJobs, status, batchJobRuns, count);
    }
    
    public static void showRunning(int index) {
        try {
            BatchJob batchJob = BatchManager.getInstance().getRunningJobs().get(index - 1);
            BatchJobRun batchJobRun = batchJob.getBatchJobRun();
            batchJobRun.log = batchJob.getBatchJobLogger().toString();
            renderTemplate("@show", batchJobRun);
        } catch (IndexOutOfBoundsException e) {
            notFound();
        }
    } 
    
    public static void show(Long id) {
        notFoundIfNull(id);
        BatchJobRun batchJobRun = BatchJobRun.findById(id);
        notFoundIfNull(batchJobRun);

        render(batchJobRun);
    }
    
    public static void acknowledge(Long batchJobRunId) {
        notFoundIfNull(batchJobRunId);
        BatchJobRun batchJobRun = BatchJobRun.findById(batchJobRunId);
        notFoundIfNull(batchJobRunId);
        batchJobRun.acknowledged = true;
        batchJobRun.save();
        index(1);
    }
}
