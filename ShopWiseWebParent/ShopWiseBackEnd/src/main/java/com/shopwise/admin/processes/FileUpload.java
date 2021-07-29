package com.shopwise.admin.processes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUpload {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpload.class);

    public static void saveFile(String dir, String fileName, MultipartFile multipartFile) throws IOException {
        LOGGER.info("FileUpload | saveFile is started");

        Path uploadPath = Paths.get(dir);

        LOGGER.info("FileUpload | saveFile | uploadPath : " + uploadPath);

        LOGGER.info("FileUpload | saveFile | Files.exists(uploadPath) : " + Files.exists(uploadPath));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            LOGGER.info("FileUpload | saveFile | Files.createDirectories is called");
        }

        try (InputStream in = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            LOGGER.info("FileUpload | saveFile | filePath : " + filePath);

            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("FileUpload | saveFile | Files.copy is called");
        } catch (Throwable t) {
            LOGGER.error("Could not save File", t);

            throw new IOException("Could not save file: " + fileName, t);
        }
    }

    public static void cleanDir(String dir) {
        LOGGER.info("FileUpload | cleanDir is started");

        LOGGER.info("FileUpload | cleanDir | dir : " + dir);

        Path dirPath = Paths.get(dir);

        LOGGER.info("FileUpload | cleanDir | dirPath : " + dirPath);

        try {
            Files.list(dirPath).forEach(file -> {

                LOGGER.info("FileUpload | cleanDir | file : " + file.toString());

                LOGGER.info("FileUpload | cleanDir | Files.isDirectory(file) : " + Files.isDirectory(file));

                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);

                        LOGGER.info("FileUpload | cleanDir | delete is completed");
                    } catch (Throwable t) {
                        LOGGER.error("FileUpload | cleanDir | ex.getMessage() : " + t.getMessage());
                        LOGGER.error("Could not delete file: " + file);
                    }
                }
            });
        } catch (Throwable t) {
            LOGGER.error("FileUpload | cleanDir | ex.getMessage() : " + t.getMessage());
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    public static void removeDir(String dir) {

        LOGGER.info("FileUpload | removeDir is started");

        LOGGER.info("FileUpload | removeDir | dir : " + dir);

        cleanDir(dir);

        LOGGER.info("FileUpload | cleanDir(dir) is over");

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            LOGGER.error("Could not remove directory: " + dir);
        }

    }
}
