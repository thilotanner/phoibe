package util.migration;

import com.googlecode.flyway.core.Flyway;
import play.Logger;
import play.PlayPlugin;
import play.db.DB;

public class MigrationPlugin extends PlayPlugin
{
    @Override
    public void onApplicationStart()
    {
        Logger.info("Migrate Datasource: " + DB.datasource);
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB.datasource);
        flyway.setDisableInitCheck(true);
        int appliedMigrations = flyway.migrate();
        Logger.info(appliedMigrations + " migrations applied");
    }
}
