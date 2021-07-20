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
        LOGGER.info("FileUploadUtil | saveFile is started");

        Path uploadPath = Paths.get(dir);

        LOGGER.info("FileUploadUtil | saveFile | uploadPath : " + uploadPath);

        LOGGER.info("FileUploadUtil | saveFile | Files.exists(uploadPath) : " + Files.exists(uploadPath));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            LOGGER.info("FileUploadUtil | saveFile | Files.createDirectories is called");
        }

        try (InputStream in = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            LOGGER.info("FileUploadUtil | saveFile | filePath : " + filePath);

            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("FileUploadUtil | saveFile | Files.copy is called");
        } catch (Throwable t) {
            LOGGER.error("Could not save File", t);

            throw new IOException("Could not save file: " + fileName, t);
        }
    }

    public static void cleanDir(String dir) {
        LOGGER.info("FileUploadUtil | cleanDir is started");

        LOGGER.info("FileUploadUtil | cleanDir | dir : " + dir);

        Path dirPath = Paths.get(dir);

        LOGGER.info("FileUploadUtil | cleanDir | dirPath : " + dirPath);

        try {
            Files.list(dirPath).forEach(file -> {

                LOGGER.info("FileUploadUtil | cleanDir | file : " + file.toString());

                LOGGER.info("FileUploadUtil | cleanDir | Files.isDirectory(file) : " + Files.isDirectory(file));

                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);

                        LOGGER.info("FileUploadUtil | cleanDir | delete is completed");
                    } catch (Throwable t) {
                        LOGGER.error("FileUploadUtil | cleanDir | ex.getMessage() : " + t.getMessage());
                        LOGGER.error("Could not delete file: " + file);
                    }
                }
            });
        } catch (Throwable t) {
            LOGGER.error("FileUploadUtil | cleanDir | ex.getMessage() : " + t.getMessage());
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    public static void removeDir(String dir) {

        LOGGER.info("FileUploadUtil | removeDir is started");

        LOGGER.info("FileUploadUtil | removeDir | dir : " + dir);

        cleanDir(dir);

        LOGGER.info("FileUploadUtil | cleanDir(dir) is over");

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            LOGGER.error("Could not remove directory: " + dir);
        }

    }
}
