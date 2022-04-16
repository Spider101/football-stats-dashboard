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
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class FileUploadServiceTest {
    private static final Path PATH_TO_RESOURCES_DIR =  Paths.get(System.getProperty("user.dir"),
            "src", "test", "resources");
    private static final Path PATH_TO_UPLOAD_DIR = PATH_TO_RESOURCES_DIR.resolve("uploads");
    private static final int BYTES_IN_MEGABYTE = 1024;

    private FileUploadService fileUploadService;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        FileUploadConfiguration fileUploadConfiguration = new FileUploadConfiguration();
        fileUploadConfiguration.setAllowedMediaTypes(ImmutableList.of("image/jpeg", "image/png"));
        fileUploadConfiguration.setMaxSizeInBytes((long) (BYTES_IN_MEGABYTE * BYTES_IN_MEGABYTE));
        fileUploadConfiguration.setUploadPath(Paths.get("src", "test", "resources", "uploads").toString());

        fileUploadService = new FileUploadService(fileUploadConfiguration);
        fileUploadService.initializeService();
    }

    @Test
    public void storeFilePersistsImageFileInputStreamToDisk() throws IOException {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileToUpload = imageFileName + ".jpeg";
        String fileKey = null;
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(imageFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(imageFileToUpload))) {
            fileKey = fileUploadService.storeFile(fileStream, imageFileToUpload, "image/jpeg", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }

        // assert
        assertTrue(fileKey != null && fileKey.contains(imageFileName));
        assertTrue(Files.exists(PATH_TO_UPLOAD_DIR.resolve(fileKey)));
    }

    @Test(expected = ServiceException.class)
    public void storeFileDoesNotPersistTextFileInputStream() throws IOException {
        // setup
        String textFileToUpload = "textFileToUpload.txt";
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(textFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(textFileToUpload))) {
            fileUploadService.storeFile(fileStream, textFileToUpload, "text/plain", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }
    }

    @Test(expected = ServiceException.class)
    public void storeFileDoesNotPersistLargeImageFileInputStreamToDisk() throws IOException {
        // setup
        String largeImageFileToUpload = "largeImageFile.png";
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(largeImageFileToUpload));

        // execute
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(largeImageFileToUpload))) {
            fileUploadService.storeFile(fileStream, largeImageFileToUpload, "image/png", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }
    }

    @Test
    public void doesFileExistReturnsTrueForAValidFileKey() throws IOException {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileExtension = ".jpeg";
        String fileKey = imageFileName + UUID.randomUUID() + imageFileExtension;
        Files.copy(PATH_TO_RESOURCES_DIR.resolve(imageFileName + imageFileExtension),
                PATH_TO_UPLOAD_DIR.resolve(fileKey));

        // execute
        boolean doesFileExist = fileUploadService.doesFileExist(fileKey);

        // assert
        assertTrue(doesFileExist);
    }

    @Test
    public void doesFileExistReturnsTrueForInvalidFileKey() {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileExtension = ".jpeg";
        String maliciousFileKey = "../" + imageFileName + imageFileExtension;

        // execute
        boolean doesFileExist = fileUploadService.doesFileExist(maliciousFileKey);

        // assert
        assertFalse(doesFileExist);
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        if (Files.exists(PATH_TO_UPLOAD_DIR)) {
            Files.walk(PATH_TO_UPLOAD_DIR)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            assertFalse("Directory still exists", Files.exists(PATH_TO_UPLOAD_DIR));
        }
    }
}