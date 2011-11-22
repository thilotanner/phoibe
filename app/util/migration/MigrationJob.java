package util.migration;

import com.googlecode.flyway.core.Flyway;
import play.Logger;
import play.db.DB;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class MigrationJob extends Job
{
    @Override
    public void doJob() throws Exception
    {
        Logger.info("Migrate Datasource: " + DB.datasource);
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB.datasource);
        flyway.setDisableInitCheck(true);
        int appliedMigrations = flyway.migrate();
        Logger.info(appliedMigrations + " migrations applied");
    }
}
