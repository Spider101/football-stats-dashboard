package com.footballstatsdashboard.services;

import java.io.InputStream;

public interface IFileUploadService {
    void initializeService();
    String storeFile(InputStream fileStream, String fileName, String mediaType, long fileSizeInBytes);
    boolean doesFileExist(String fileKey);
}