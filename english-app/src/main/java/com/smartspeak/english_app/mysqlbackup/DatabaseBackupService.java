package com.smartspeak.english_app.mysqlbackup;

import com.smattme.MysqlExportService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class DatabaseBackupService {
    private final MysqlExportService mysqlExportService;
    private final String oAuthToken;

    public DatabaseBackupService(String dbName, String username, String password,
                                 String dbHost, String dbPort, String oAuthToken) {
        this.oAuthToken = oAuthToken;
        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, dbName);
        properties.setProperty(MysqlExportService.DB_USERNAME, username);
        properties.setProperty(MysqlExportService.DB_PASSWORD, password);
        properties.setProperty(MysqlExportService.DB_HOST, dbHost);
        properties.setProperty(MysqlExportService.DB_PORT, dbPort);
        properties.setProperty(MysqlExportService.TEMP_DIR, new File("backup").getAbsolutePath());
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_SQL_FILE, "false");

        mysqlExportService = new MysqlExportService(properties);
    }

    public void backupDatabase() {
        try {
            mysqlExportService.export();
            File zipFile = mysqlExportService.getGeneratedZipFile();
            uploadToYandexDisk(zipFile);
            System.out.println("Backup created and uploaded: " + zipFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadToYandexDisk(File file) {
        try {
            // Шаг 1: Получаем URL для загрузки
            String uploadUrl = getUploadUrl(file.getName());

            // Шаг 2: Загружаем файл
            if (uploadUrl != null) {
                HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Authorization", "OAuth " + oAuthToken);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream(); FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("File uploaded successfully");
                    // Удаление файла после успешной загрузки
                    if (file.delete()) {
                        System.out.println("File deleted successfully: " + file.getName());
                    } else {
                        System.out.println("Failed to delete the file: " + file.getName());
                    }
                } else {
                    System.out.println("Error during upload: " + responseCode);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        System.out.println("Response: " + response.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getUploadUrl(String fileName) {
        String uploadUrl = null;
        try {
            // Запрос на получение URL для загрузки
            String requestUrl = "https://cloud-api.yandex.net/v1/disk/resources/upload?path=/backup/" + fileName + "&overwrite=true";
            HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "OAuth " + oAuthToken);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    uploadUrl = response.toString(); // Получаем URL из ответа
                    uploadUrl = uploadUrl.substring(uploadUrl.indexOf("href\":\"") + 7, uploadUrl.indexOf("\"", uploadUrl.indexOf("href\":\"") + 7)); // Извлекаем значение href
                }
            } else {
                System.out.println("Error getting upload URL: " + connection.getResponseCode());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("Response: " + response.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadUrl;
    }
}
