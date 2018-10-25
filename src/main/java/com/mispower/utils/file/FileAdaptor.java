package com.mispower.utils.file;

import com.google.common.base.Preconditions;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * FileAdaptor provides read and write access to a variety of files.
 * For consistency, the file suffix must be preserved
 *
 * @author wuguolin
 */
public class FileAdaptor {

    /**
     * input file
     */
    private final File file;
    /**
     * max buffer size for every read,used for read big file.
     */
    private final int maxReadBuffer;
    /**
     * output directory. If empty,will not write output
     */
    private final String outputDir;
    /**
     * file suffix.To consistency,write output file will be remain same suffix as input file.
     */
    private String suffix;
    /**
     * file name without suffix
     */
    private String fileNameWithoutSuf;

    private final int fileHasCode;

    /**
     * read buffer
     */
    private final byte[] buffer;


    private final LinkedList<byte[]> dataQueue = new LinkedList<>();

//    private final AtomicInteger maxBufferSize = new AtomicInteger(1024);
//
//    private final AtomicInteger maxBufferSize = new AtomicInteger(1000);

    /**
     * dot of file suffix
     */
    private final String SEPARATOR = ".";

    private FileAdaptor(File file, int maxReadBuffer, String outputDir) {
        this.file = file;
        this.fileHasCode = file.hashCode();
        this.maxReadBuffer = maxReadBuffer;
        this.outputDir = outputDir;
        buffer = new byte[maxReadBuffer];
        init();
    }

    private void init() {
        String fileName = file.getName();
        int dot = fileName.lastIndexOf(SEPARATOR);
        suffix = fileName.substring(dot + 1);
        fileNameWithoutSuf = fileName.substring(0, dot);

    }

    /**
     * read file to buffer stream
     *
     * @return
     */
    public int read() {
        try {
            Long start = System.currentTimeMillis();
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            int len = 0;
            byte[] copy;
            while ((len=bufferedInputStream.read(buffer)) != -1) {
                copy=new byte[len];
                System.arraycopy(buffer,0,copy,0,len);
                offerQueue(copy);

            }
            Long end = System.currentTimeMillis();
            System.out.println("use time:" + maxReadBuffer + ": leap size:" + ":" + (end - start));
            bufferedInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void offerQueue(byte[] data) {
        if (data != null) {
            synchronized (dataQueue) {
                dataQueue.offer(data);
            }
        }
    }

    public void doProcess() {
        write();

    }

    private void write() {
        File writeFile = preWrite();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(writeFile, true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            final LinkedBlockingQueue<byte[]> tmp = new LinkedBlockingQueue<>();
            synchronized (dataQueue) {
                tmp.addAll(dataQueue);
                dataQueue.clear();
            }
            for (byte[] data : tmp) {
                bufferedOutputStream.write(data, 0, data.length);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private File preWrite() {
        Preconditions.checkNotNull(outputDir, "outputDir must be not empty if want to write");
        final File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        Preconditions.checkState(outDir.isDirectory(),
                "Path is not a directory: " + outDir.getAbsolutePath());
        StringBuffer sb = new StringBuffer();
        sb.append(outputDir);
        sb.append("/");
        sb.append(fileHasCode);
        sb.append(SEPARATOR);
        sb.append(suffix);
        return new File(sb.toString());
    }


    public String getSuffix() {
        return suffix;
    }

    public String getFileName() {
        return fileNameWithoutSuf;
    }

    /**
     * Builder Pattern
     */
    public static class Builder {
        private String inputFile;
        private int maxReadBuffer = 1024;
        private String outputDir;

        public Builder setInputFile(String inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public Builder setMaxReadBuffer(int maxReadBuffer) {
            this.maxReadBuffer = maxReadBuffer;
            return this;
        }

        public Builder setOutputDir(String outputDir) {
            this.outputDir = outputDir;
            return this;
        }


        public FileAdaptor build() {
            // Sanity checks
            Preconditions.checkNotNull(inputFile, "must be specify input path");
            File file = new File(inputFile);
            Preconditions.checkState(file.isFile(), "The parameter type must be a file");
            return new FileAdaptor(file, maxReadBuffer, outputDir);
        }
    }


}
