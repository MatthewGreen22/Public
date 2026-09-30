package com.smartspeak.english_app.mysqlbackup;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final DatabaseBackupService backupService;

    public BackupScheduler(DatabaseBackupService backupService) {
        this.backupService = backupService;
    }

    public void startScheduledBackup(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(backupService::backupDatabase, initialDelay, period, unit);
    }
}
