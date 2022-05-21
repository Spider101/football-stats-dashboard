package com.footballstatsdashboard.services;

import com.footballstatsdashboard.config.FileUploadConfiguration;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.google.common.collect.ImmutableList;
import org.eclipse.jetty.http.HttpStatus;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class FileStorageServiceTest {
    private static final Path PATH_TO_RESOURCES_DIR =  Paths.get(System.getProperty("user.dir"),
            "src", "test", "resources");
    private static final Path PATH_TO_UPLOAD_DIR = PATH_TO_RESOURCES_DIR.resolve("uploads");
    private static final int BYTES_IN_MEGABYTE = 1024;

    private FileStorageService fileUploadService;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        FileUploadConfiguration fileUploadConfiguration = new FileUploadConfiguration();
        fileUploadConfiguration.setAllowedMediaTypes(ImmutableList.of("image/jpeg", "image/png"));
        fileUploadConfiguration.setMaxSizeInBytes((long) (BYTES_IN_MEGABYTE * BYTES_IN_MEGABYTE));
        fileUploadConfiguration.setUploadPath(Paths.get("src", "test", "resources", "uploads").toString());

        fileUploadService = new FileStorageService(fileUploadConfiguration);
        fileUploadService.initializeService();
    }

    /**
     * given a valid input stream from an image file, tests that the stream is persisted to disk and a file key
     * referencing that is returned
     */
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

    /**
     * given an input stream for a text file, tests that the input stream is not persisted to disk and a service
     * exception is thrown instead
     */
    @Test
    public void storeFileDoesNotPersistTextFileInputStream() throws IOException {
        // setup
        String textFileToUpload = "textFileToUpload.txt";
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(textFileToUpload));
        ServiceException serviceException = null;

        // execute
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(textFileToUpload))) {
            serviceException = assertThrows(ServiceException.class,
                    () -> fileUploadService.storeFile(fileStream, textFileToUpload, "text/plain", fileSizeInBytes));
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }

        // assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given an input stream for a text file, tests that the input stream is not persisted to disk and a service
     * exception is thrown instead
     */
    @Test
    public void storeFileDoesNotPersistLargeImageFileInputStreamToDisk() throws IOException {
        // setup
        String largeImageFileToUpload = "largeImageFile.png";
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(largeImageFileToUpload));
        ServiceException serviceException = null;

        // execute
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(largeImageFileToUpload))) {
            serviceException = assertThrows(ServiceException.class,
                    () -> fileUploadService.storeFile(fileStream, largeImageFileToUpload, "image/png",
                            fileSizeInBytes));
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }

        // assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a file key for a valid file stored on disk, tests that the method verifies that and returns true
     */
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

    /**
     * given a file key for a non-existent file, tests that the method verifies that and returns false
     */
    @Test
    public void doesFileExistReturnsFalseForInvalidFileKey() {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileExtension = ".jpeg";
        String maliciousFileKey = "../" + imageFileName + imageFileExtension;

        // execute
        boolean doesFileExist = fileUploadService.doesFileExist(maliciousFileKey);

        // assert
        assertFalse(doesFileExist);
    }

    /**
     * given a file key for a non-existent file, tests that no input stream corresponding to the file is returned and a
     * service exception is thrown instead
     */
    @Test
    public void loadFileWhenNoFileFoundForFileKey() {
        // setup
        String nonExistentFileKey = "nonExistentFile.png";

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> fileUploadService.loadFile(nonExistentFileKey));

        // assert
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given a file key for a valid file stored on disk, tests that the file is loaded and the input stream
     * corresponding to the file is returned
     */
    @Test
    public void loadFileReturnsInputStreamForFileStoredOnDisk() throws IOException {
        // setup
        String imageFileName = "stockPhoto";
        String imageFileToUpload = imageFileName + ".jpeg";
        String fileKey = null;
        long fileSizeInBytes = Files.size(PATH_TO_RESOURCES_DIR.resolve(imageFileToUpload));
        try (InputStream fileStream = Files.newInputStream(PATH_TO_RESOURCES_DIR.resolve(imageFileToUpload))) {
            fileKey = fileUploadService.storeFile(fileStream, imageFileToUpload, "image/png", fileSizeInBytes);
        } catch (IOException ioException) {
            fail("Failed to save file!");
        }

        // execute
        InputStream loadedFileStream = fileUploadService.loadFile(fileKey);

        // assert
        assertNotNull(loadedFileStream);
        assertEquals(fileSizeInBytes, loadedFileStream.available());
    }

    /**
     * clean up work done at the end of the test suite's run involving removing all files creating as a result of
     * running each individual test case
     */
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