package controllers;

import play.i18n.Messages;
import util.backup.DbBackupJob;

public class Backup extends ApplicationController {
    public static void index() {
        render();
    }

    public static void backupDb() {
        DbBackupJob dbBackupJob = new DbBackupJob();
        dbBackupJob.now();

        flash.success(Messages.get("successfullyStarted", Messages.get("backup.dbBackup")));
        index();
    }
}
