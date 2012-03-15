package util.backup;

import play.Play;
import play.jobs.On;
import util.batch.BatchJob;
import util.batch.BatchJobLogger;
import util.file.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@On("0 0 23 * * ?")
public class DbBackupJob extends BatchJob {

    private static final int DEFAULT_MAX_BACKUP_COUNT = 30;
    private static final SimpleDateFormat BACKUP_FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String MAX_COUNT_CONFIG_KEY = "backup.db.maxCount";

    @Override
    public void doBatchJob(BatchJobLogger batchJobLogger) throws Exception
    {
        File backupFolder = Play.getFile("backup/");
        
        backupCleanup(backupFolder, batchJobLogger);

        backup(backupFolder, batchJobLogger);
    }

    private void backupCleanup(File backupFolder,
                               BatchJobLogger batchJobLogger)
    {
        batchJobLogger.info("Start cleanup database backups");

        int maxBackupCount;

        if(Play.configuration.containsKey(MAX_COUNT_CONFIG_KEY)) {
            maxBackupCount = Integer.parseInt((String) Play.configuration.get(MAX_COUNT_CONFIG_KEY));
            batchJobLogger.info(String.format("%s defined: %s", MAX_COUNT_CONFIG_KEY, maxBackupCount));
        } else {
            maxBackupCount = DEFAULT_MAX_BACKUP_COUNT;
            batchJobLogger.info(String.format("Use default %s:%d", MAX_COUNT_CONFIG_KEY, DEFAULT_MAX_BACKUP_COUNT));
        }

        
        
        File[] backupFiles = FileUtils.dirListByAscendingDate(backupFolder);
        batchJobLogger.info(backupFiles.length + " backup files(s) found:");
        for(File backupFile : backupFiles) {
            batchJobLogger.info(backupFile.getAbsolutePath());
        }

        if(backupFiles.length > maxBackupCount) {
            for(int i = 0; i < backupFiles.length - maxBackupCount; i++) {
                File fileToDelete = backupFiles[i];
                batchJobLogger.info("Delete backup: " + fileToDelete.getAbsolutePath());
                if(!fileToDelete.delete()) {
                    batchJobLogger.error("Unable to delete file: " + fileToDelete.getAbsolutePath());
                }
            }
        } else {
            batchJobLogger.info("Nothing to delete");
        }
    }

    private void backup(File backupFolder,
                        BatchJobLogger batchJobLogger) throws Exception
    {
        batchJobLogger.info("Start DB backup");

        String user = Play.configuration.getProperty("db.user");
        String password = Play.configuration.getProperty("db.pass");
        String database = Play.configuration.getProperty("db.url");
        Matcher matcher = Pattern.compile("/([a-z]+)(\\?|$)").matcher(database);
        if(matcher.find()) {
            database = matcher.group(1);
        } else {
            throw new RuntimeException("Unable to extract database name");
        }

        batchJobLogger.info("Database: " + database);

        String backupFileName = "DBBackup";
        backupFileName += BACKUP_FILE_DATE_FORMAT.format(new Date());
        backupFileName += ".sql";

        if(!backupFolder.exists() && !backupFolder.mkdirs()) {
            throw new RuntimeException("Cannot create backup folder: " + backupFolder.getAbsolutePath());
        }

        if(!backupFolder.canWrite()) {
            throw new RuntimeException("Backup folder is not writable: " + backupFolder.getAbsolutePath());
        }

        File backupFile = new File(backupFolder, backupFileName);

        String mysqlBinPath = Play.configuration.getProperty("mysql.bin.path");
        if(mysqlBinPath.length() > 0 && !mysqlBinPath.endsWith(File.separator)) {
            mysqlBinPath = String.format("%s%s", mysqlBinPath, File.separator);
        }
        
        String command = String.format("%smysqldump -u%s -p%s %s > %s", mysqlBinPath, user, password, database, backupFile.getAbsolutePath());

        batchJobLogger.info(String.format("Run command: %s", command));

        String[] shellCommand = {
                "/bin/sh",
                "-c",
                command
        };
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(shellCommand);
        int result = process.waitFor();

        if(result != 0) {
            throw new RuntimeException("Process failed");
        }
        
        batchJobLogger.info("Backup completed");
    }
}
