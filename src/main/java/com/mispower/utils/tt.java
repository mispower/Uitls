package com.mispower.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mispower.utils.file.BaseAsyncMultiFiles;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class tt extends BaseAsyncMultiFiles {
    private static final int maxPoolSize = Integer.MAX_VALUE;
    private static final int concurrencyCount = 32;

    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("file-task-%d").build();
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(concurrencyCount, maxPoolSize,
            100000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    @Override
    public boolean doFilter(File file) {
        return false;
    }

    public static void main(String[] args) {

    }

//
//    public static void main(String[] args) {
//        new BaseAsyncMultiFiles() {
//            @Override
//            public boolean doFilter(File file) {
//                return false;
//            }
//        };
//    }
}
