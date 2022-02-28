package com.footballstatsdashboard.services;

import com.footballstatsdashboard.config.FileUploadConfiguration;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.google.common.collect.ImmutableList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.Assert.*;

public class FileUploadServiceTest {
    private final static Path pathToResourcesDir =  Paths.get(System.getProperty("user.dir"),
            "src", "test", "resources");
    private final static Path pathToUploadDir = pathToResourcesDir.resolve("uploads");

    private FileUploadService fileUploadService;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        FileUploadConfiguration fileUploadConfiguration = new FileUploadConfiguration();
        fileUploadConfiguration.setAllowedMediaTypes(ImmutableList.of("image/jpeg", "image/png"));
        fileUploadConfiguration.setMaxSizeInBytes((long) (1024 * 1024));
        fileUploadService = new FileUploadService(fileUploadConfiguration);
        fileUploadService.initializeService(pathToUploadDir.toString());
    }

    @Test
    public void storeFilePersistsImageFileInputStreamToDisk() throws IOException {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileToUpload = imageFileName + ".jpeg";
        String fileKey = null;
        long fileSizeInBytes = Files.size(pathToResourcesDir.resolve(imageFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(pathToResourcesDir.resolve(imageFileToUpload))) {
            fileKey = fileUploadService.storeFile(fileStream, imageFileToUpload, "image/jpeg", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }

        // assert
        assertTrue(fileKey != null && fileKey.contains(imageFileName));
        assertTrue(Files.exists(pathToUploadDir.resolve(fileKey)));
    }

    @Test(expected = ServiceException.class)
    public void storeFileDoesNotPersistTextFileInputStream() throws IOException {
        // setup
        String textFileToUpload = "textFileToUpload.txt";
        long fileSizeInBytes = Files.size(pathToResourcesDir.resolve(textFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(pathToResourcesDir.resolve(textFileToUpload))) {
            fileUploadService.storeFile(fileStream, textFileToUpload, "text/plain", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }
    }

    @Test(expected = ServiceException.class)
    public void storeFileDoesNotPersistLargeImageFileInputStreamToDisk() throws IOException {
        // setup
        String largeImageFileToUpload = "largeImageFile.png";
        long fileSizeInBytes = Files.size(pathToResourcesDir.resolve(largeImageFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(pathToResourcesDir.resolve(largeImageFileToUpload))) {
            fileUploadService.storeFile(fileStream, largeImageFileToUpload, "image/png", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        if (Files.exists(pathToUploadDir)){
            Files.walk(pathToUploadDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            assertFalse("Directory still exists", Files.exists(pathToUploadDir));
        }
    }
}