package com.mispower.utils.file;

import com.google.common.base.Preconditions;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FileAdaptor provides read and write access to a variety of files.
 * For consistency, the file suffix must be preserved.
 * For performance ,this class extends BufferedInputStream
 *
 * @author wuguolin
 * @see java.io.BufferedInputStream
 */
public class FileAdaptor extends BufferedInputStream {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * input file
     */
    private final File file;

    /**
     * file suffix.To consistency,write output file will be remain same suffix as input file.
     */
    private String suffix;
    /**
     * file name without suffix
     */
    private String fileNameWithoutSuf;

    /**
     * Digest of file context,used to filter same file.
     */
    private MessageDigest mMessageDigest = null;

    /**
     * instance FileAdaptor
     *
     * @param file file
     */
    private FileAdaptor(File file) throws FileNotFoundException {
        this(file, DEFAULT_BUFFER_SIZE);
    }

    private FileAdaptor(File file, int size) throws FileNotFoundException {
        super(new FileInputStream(file), size);
        this.file = file;
        init();
    }

    /**
     * initialize
     */
    private void init() {
        final String separator = ".";
        final String fileName = file.getName();
        int dot = fileName.lastIndexOf(separator);
        suffix = fileName.substring(dot + 1);
        fileNameWithoutSuf = fileName.substring(0, dot);
        try {
            mMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get file suffix.
     *
     * @return file suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Get file name
     *
     * @return String
     */
    public String getFileName() {
        return fileNameWithoutSuf;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }


    /**
     * Get the MD5 of the file.Used to compare files which have different name contains same content or not.
     */
    public String getFileMD5() {
        try {
            int maxReadBuffer = DEFAULT_BUFFER_SIZE >> 3;
            byte[] buffer = new byte[maxReadBuffer];
            int length;
            while ((length = read(buffer, 0, maxReadBuffer)) != -1) {
                mMessageDigest.update(buffer, 0, length);
            }
            return new BigInteger(1, mMessageDigest.digest()).toString(16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static class Builder {
        private File file;

        public Builder setFile(File file) {
            this.file = file;
            return this;
        }

        public FileAdaptor build() {
            Preconditions.checkState(file.isFile(), "input must be a file");
            FileAdaptor fileAdaptor = null;
            try {
                fileAdaptor = new FileAdaptor(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return fileAdaptor;
        }

    }
}
