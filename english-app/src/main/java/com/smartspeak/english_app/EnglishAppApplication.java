package com.smartspeak.english_app;

import com.smartspeak.english_app.mysqlbackup.BackupScheduler;
import com.smartspeak.english_app.mysqlbackup.DatabaseBackupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class EnglishAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishAppApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "backup.enabled", havingValue = "true")
	public DatabaseBackupService backupService(
			@Value("${backup.database.name}") String dbName,
			@Value("${backup.database.username}") String username,
			@Value("${backup.database.password}") String password,
			@Value("${backup.database.host}") String dbHost,
			@Value("${backup.database.port}") String dbPort,
			@Value("${backup.yandex-disk.token}") String oAuthToken) {
		return new DatabaseBackupService(dbName, username, password, dbHost, dbPort, oAuthToken);
	}

	@Bean
	@ConditionalOnProperty(name = "backup.enabled", havingValue = "true")
	public BackupScheduler backupScheduler(DatabaseBackupService backupService) {
		BackupScheduler scheduler = new BackupScheduler(backupService);

		// Запуск резервного копирования каждые 6 часов
		scheduler.startScheduledBackup(0, 6, TimeUnit.HOURS);

		return scheduler;
	}
}
