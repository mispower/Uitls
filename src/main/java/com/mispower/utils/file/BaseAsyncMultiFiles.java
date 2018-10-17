package com.mispower.utils.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * multi files operator
 *
 * @author wuguolin
 */
public abstract class BaseAsyncMultiFiles implements Runnable {

    /**
     * directory of monitor,this parameter must be special
     */
    private String dirPath;
    /**
     * interval for period
     */
    private long interval = 10000L;
    /**
     * policy when the file has been dealt.Default is "delete".
     */
    private PolicyEnum policy;
    /**
     * parallel thread num
     */
    private int parallel = 1;

    /**
     * max files of per thread
     */
    private int maxFilesPerExcutor = 10;

    /**
     * original file array
     */
    private List<File> original;

    /**
     * list of expire file's name
     */
    private List<String> expireFiles;

    /**
     * list of active  file's name
     */
    private List<String> activeFiles;
    /**
     * if policy is "change", change file postfix to this when the file has been dealt
     */
    private String completed;


    /**
     * increase
     */
    private AtomicInteger increase = new AtomicInteger(0);


    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void setInterval(long interval) {
        this.interval = interval;

    }

    public void setPolicy(PolicyEnum policy) {
        this.policy = policy;

    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }


    /**
     * monitor special directory
     */
    public void monitor() {
        File file = new File(dirPath);
        do {
            synchronized (original) {
                original.addAll(listFiles(file));
            }
            increase.addAndGet(1);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (true);
    }

    public void doWork() {

    }

    @Override
    public void run() {

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
                boolean bl = activeFiles.contains(fileName) && expireFiles.contains(fileName);
                return bl && doFilter(file);
            }
        };

    }

    /**
     * file filter.defined filter logic according  your business.if you do nothing return true
     *
     * @param file
     * @return boolean
     */
    public abstract boolean doFilter(File file);


    /**
     * Policy enum
     */
    protected enum PolicyEnum {

        /**
         * policy:"change"
         */
        CHANGE("change", 1),

        /**
         * policy:"delete"
         */
        DELETE("delete", 2);

        private String enumName;
        private int index;

        PolicyEnum(String enumName, int index) {
            this.enumName = enumName;
            this.index = index;
        }

        public static int getIndexByName(String enumName) {
            return PolicyEnum.valueOf(enumName).index;
        }

        public static String getNameByIndex(int index) {
            for (PolicyEnum c : PolicyEnum.values()) {
                if (c.index == index) {
                    return c.enumName;
                }
            }
            return null;

        }
    }

}
