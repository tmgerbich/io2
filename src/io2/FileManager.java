package io2;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private Path path;

    public FileManager() {
        this.path = Paths.get(System.getProperty("user.home")); // Default to user's home directory
    }

    public FileManager(String path) {
        this.path = Paths.get(path);
    }

    public ArrayList<File> listDirectoryContents() throws IOException {
        ArrayList<File> contents = new ArrayList<File>();
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        for (Path path : directoryStream) {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            File file = new File(path.getFileName(), attrs.size(), attrs.lastModifiedTime(), attrs.isDirectory());
            contents.add(file);
        }
        return contents;

    }

    public Path getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
    }
}
