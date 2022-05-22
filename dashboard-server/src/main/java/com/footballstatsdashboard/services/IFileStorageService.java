package com.footballstatsdashboard.services;

import java.io.IOException;
import java.io.InputStream;

public interface IFileStorageService {
    void initializeService();
    String storeFile(InputStream fileStream, String fileName, String mediaType, long fileSizeInBytes);
    boolean doesFileExist(String fileKey);
    InputStream loadFile(String fileKey) throws IOException;
}