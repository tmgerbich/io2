package io2;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class File {
    Path filePath;
    long fileSize;
    FileTime lastModifiedTime;
    boolean isDirectory;

    public File(Path filePath, long fileSize, FileTime lastModifiedTime, boolean isDirectory) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.lastModifiedTime = lastModifiedTime;
        this.isDirectory = isDirectory;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFileName(Path filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
