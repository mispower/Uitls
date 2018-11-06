package com.mispower.utils.file;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * multi files operator
 *
 * @author wuguolin
 */
public class MultiFiles {

    private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
    /**
     * directory of monitor,this parameter must be special
     */
    private String dirPath;
    /**
     * interval for period
     */
    private long period;

    /**
     * policy when the file has been dealt.Default is "delete".
     */
    private PolicyEnum policy;
    /**
     * parallel thread num
     */
    private int parallelSize;

    /**
     * block queue size
     */
    private int blockingSize;

    /**
     * max files of per thread
     */
    private int maxFilesPerExecutor;

    /**
     * original file array
     */
    private List<File> original = new LinkedList<>();

    /**
     * list of expire file's name
     */
    private final List<String> expireFiles = new LinkedList<>();

    /**
     * list of active  file's name
     */
    private final List<String> activeFiles = new LinkedList<>();
    /**
     * if policy is "change", change file postfix to this when the file has been dealt
     */
    private String completed;
    /**
     * polling times
     */
    private AtomicInteger increase = new AtomicInteger(0);


    private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("file-task-%d").build();
    private final ThreadPoolExecutor THREAD_POOL;

    /**
     * constructor
     *
     * @param dirPath             dirPath
     * @param period              period
     * @param coreThreads         coreThreads
     * @param queueSize           queueSize
     * @param maxFilesPerExecutor maxFilesPerExecutor
     * @param policy              policy
     * @param completed           completed
     */
    private MultiFiles(String dirPath, long period, int coreThreads, int queueSize, int maxFilesPerExecutor,
                       PolicyEnum policy, String completed) {
        this.dirPath = dirPath;
        this.period = period;
        this.parallelSize = coreThreads;
        this.blockingSize = queueSize;
        this.maxFilesPerExecutor = maxFilesPerExecutor;
        this.policy = policy;
        this.completed = completed;
        THREAD_POOL = new ThreadPoolExecutor(parallelSize, MAX_POOL_SIZE, 100000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(blockingSize), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * Monitor special directory.
     */
    public void monitor() {
        while (true) {
            assignProcess();
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * assign data to each operator thread
     */
    public void assignProcess() {
        final File file = new File(dirPath);
        int assignThread;
        int size;
        original.addAll(listFiles(file));
        size = original.size();
        increase.addAndGet(1);
        if (size <= 0) {
            return;
        }
        LinkedList<File> tem = new LinkedList<>();
        if (size <= maxFilesPerExecutor) {
            tem.addAll(original);
            //copy repartition
            THREAD_POOL.execute(new DataProcess((List) tem.clone()));
        } else {
            assignThread = calculateThreadNum();
            int len;
            int start;

            for (int i = 0; i < assignThread; i++) {
                start = i * maxFilesPerExecutor;
                len = start + maxFilesPerExecutor;
                if (len > size) {
                    len = size;
                }
                tem.addAll(original.subList(start, len));
                //copy repartition
                THREAD_POOL.execute(new DataProcess((List) tem.clone()));
                tem.clear();
            }
        }
        for (File f : original) {
            activeFiles.add(f.getName());
        }
        original.clear();
    }

    /**
     * close
     */
    public void close() {
        THREAD_POOL.shutdown();
        try {
            THREAD_POOL.awaitTermination(period, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * calculate how much threads we need
     */
    private int calculateThreadNum() {
        return original.size() / maxFilesPerExecutor + 1;
    }

    /**
     * to convert filter result to List
     *
     * @param file File Object
     * @return List<File>
     */
    private List<File> listFiles(File file) {
        File[] files = file.listFiles(filter());
        assert files != null;
        return Arrays.asList(files);
    }

    /**
     * file filter
     *
     * @return FilenameFilter
     */
    private FileFilter filter() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                boolean bl = activeFiles.contains(fileName) || expireFiles.contains(fileName) || fileName.contains(completed);
                return !bl && doFilter(file);
            }
        };
    }

    /**
     * file filter.Defined filter logic according  your business.if you do nothing return true
     *
     * @param file
     * @return boolean
     */
    public boolean doFilter(File file) {
        return file.isFile();
    }

    public static class Builder {
        /**
         * directory of monitor,this parameter must be special
         */
        private String dirPath;
        /**
         * interval for period
         */
        private long period = 1000L;
        /**
         * policy when the file has been dealt.Default is "delete".
         */
        private PolicyEnum policy = PolicyEnum.DELETE;
        /**
         * parallel thread num
         */
        private int parallelSize = 10;

        /**
         * block queue size
         */
        private int bufferSize = 32;

        /**
         * max files of per thread
         */
        private int maxFilesPerExecutor = 10;

        /**
         * if policy is "change", change file postfix to this when the file has been dealt
         */
        private String completed = "COMPLETED";

        public Builder setDirPath(String dirPath) {
            this.dirPath = dirPath;
            return this;
        }

        public Builder setPeriod(Long period) {
            this.period = period;
            return this;
        }

        public Builder setPolicy(String policy) {
            this.policy = PolicyEnum.valueOf(policy.toUpperCase());
            return this;
        }

        public Builder setParallel(int parallel) {
            this.parallelSize = parallel;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setMaxFilesPerExecutor(int fileSizePerExecutor) {
            this.maxFilesPerExecutor = fileSizePerExecutor;
            return this;
        }

        public Builder setSuffix(String suffix) {
            this.completed = suffix;
            return this;
        }

        public MultiFiles build() {
            Preconditions.checkNotNull(dirPath, "parameter must be not empiry");
            return new MultiFiles(dirPath, period, parallelSize, bufferSize, maxFilesPerExecutor, policy, completed);
        }
    }

}
