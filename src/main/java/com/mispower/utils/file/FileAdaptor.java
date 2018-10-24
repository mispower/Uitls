package com.mispower.utils.file;

import com.sun.istack.internal.NotNull;

import java.io.File;

/**
 * FileAdaptor provides read and write access to a variety of files.
 * For consistency, the file suffix must be preserved
 *
 * @author wuguolin
 */
public class FileAdaptor {
    private String suffix;
    private String sourcePath;
    private String targetPath;
    private String fileName;

    private byte[] buffer;

    private FileAdaptor(String sourcePath,  String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public byte[] read() {
        File file = new File(sourcePath);
        if (file.isFile()) {
            fileName = file.getName();

        }

        return null;
    }

    public void write() {

    }

    public String getSuffix() {
        int dot = sourcePath.lastIndexOf(".");
        return sourcePath.substring(dot + 1);
    }

    public static class Builder {
        private String sourcePath;
        private String targetPath;

        public Builder setTargetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        public Builder setSourcePath(String sourcePath) {
            this.sourcePath = sourcePath;
            return this;
        }

        public FileAdaptor build() {

            return new FileAdaptor(sourcePath, null);
        }
    }


}
