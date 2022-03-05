package com.footballstatsdashboard.services;

import com.footballstatsdashboard.config.FileUploadConfiguration;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public class FileUploadService implements IFileUploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);

    private Path uploadPath;
    private final FileUploadConfiguration fileUploadConfiguration;

    public FileUploadService(FileUploadConfiguration fileUploadConfiguration) {
        this.fileUploadConfiguration = fileUploadConfiguration;
    }

    public void initializeService() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initializing file upload service...");
        }

        uploadPath = Paths.get(System.getProperty("user.dir"), this.fileUploadConfiguration.getUploadPath());
        if (!Files.exists(uploadPath) || !Files.isDirectory(uploadPath)) {
            try {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("No existing directory found at {}. Creating it now...", uploadPath);
                }
                Files.createDirectory(uploadPath);
            } catch (IOException ioException) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500,
                        "Failed to create folder for storing uploaded images!");
            }
        }
    }

    public String storeFile(InputStream fileStream, String fileName, String mediaType, long fileSizeInBytes) {
        Optional<String> fileExtension = getExtensionFromFileName(fileName);
        if (fileExtension.isEmpty() ||
                !this.fileUploadConfiguration.getAllowedMediaTypes().contains(mediaType)) {
            String errorMessage = String.format("File media type must be one of %s. Found %s",
                    this.fileUploadConfiguration.getAllowedMediaTypes(), mediaType);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, errorMessage);
        }

        if (fileSizeInBytes > this.fileUploadConfiguration.getMaxSizeInBytes()) {
            String errorMessage = String.format("File size is larger than maximum allowed value of %d bytes!",
                    this.fileUploadConfiguration.getMaxSizeInBytes());
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, errorMessage);
        }

        // replace the extension (like .jpeg) with a delimiter
        // then append a random UUID and the file extension back again
        String fileKey = fileName.replace(fileExtension.get(), "_") + UUID.randomUUID() + fileExtension.get();
        try {
            Files.copy(fileStream, this.uploadPath.resolve(fileKey));
        } catch (IOException e) {
            throw new RuntimeException("Could not save file stream to location: " + this.uploadPath);
        }
        return fileKey;
    }

    public boolean doesFileExist(String fileKey) {
        return Files.exists(this.uploadPath.resolve(fileKey));
    }

    // TODO: 04/03/22 add method for loading file input stream from disk to stream to client

    private Optional<String> getExtensionFromFileName(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".")));
    }
}