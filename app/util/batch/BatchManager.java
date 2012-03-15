////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) Astina AG. All Rights Reserved.
//
// This software is the confidential and proprietary information of Astina AG
// ("Confidential Information"). You shall not disclose such Confidential Information and shall use
// it only in accordance with the terms of the license agreement you entered into with Astina.
//
// ASTINA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ASTINA SHALL NOT BE LIABLE FOR ANY DAMAGES
// SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
// DERIVATIVES.
////////////////////////////////////////////////////////////////////////////////////////////////////
package util.batch;

import play.Play;
import play.PlayPlugin;
import play.jobs.JobsPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: ttanner $
 * @version $Revision: 1853 $, $Date: 2011-02-15 16:02:41 +0100 (Di, 15 Feb 2011) $
 */
public class BatchManager
{
    private static BatchManager batchManager;
    private List<BatchJob> runningJobs;
    private JobsPlugin jobsPlugin;

    public static BatchManager getInstance()
    {
        if(batchManager == null) {
            batchManager = new BatchManager();
        }
        return batchManager;
    }

    private BatchManager()
    {
        this.runningJobs = new ArrayList<BatchJob>();

        jobsPlugin = Play.plugin(JobsPlugin.class);
    }

    protected synchronized void addRunningBatchJob(BatchJob batchJob)
    {
        this.runningJobs.add(batchJob);
    }

    protected synchronized void removeRunningBatchJob(BatchJob batchJob)
    {
        this.runningJobs.remove(batchJob);
    }

    public List<BatchJob> getRunningJobs()
    {
        return runningJobs;
    }

    public String getStatus()
    {
        return jobsPlugin.getStatus();        
    }
}
