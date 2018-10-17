import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class testThread implements Runnable {
    private String name;

    testThread(String name) {
        this.name = name;
    }

    private static final int maxPoolSize = Integer.MAX_VALUE;
    private static final int concurrencyCount = 32;

    static List<String> ii = new ArrayList<>();
    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("monitor_%d").build();
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(concurrencyCount, maxPoolSize,
            100000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws InterruptedException {
//        for (int i = 0; i <= 10; i++) {
//            threadPool.execute(new testThread(1 + ""));
//            threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
//        }
//
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
//                synchronized (ii) {
                while (true) {
                    System.out.println(i);
                    System.out.println("ddd:" + threadPool.getActiveCount());
                    if (ii.size() > 0) {
                        ii.remove(0);
                    }
                    if (ii.size() < 3 && ii.size() > 0) {
                        this.notifyAll();
                    }

                    i++;
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    }
                }
            }
        });
        List<Runnable> tasks = Lists.newArrayList(new testThread("11"), t);
        for (int i = 0; i < 10; i++) {
            tasks.add(new testThread(i + ""));
        }


        for (Runnable task : tasks) {
            threadPool.execute(task);
        }
    }

    @Override
    public void run() {
        synchronized (ii) {
            System.out.println("==========");
            System.out.println(Thread.currentThread().getName());
            System.out.println("==========");

            int i = 0;
            while (ii.size() < 3) {
                ii.add(Thread.currentThread().getName() + "-" + name + "_" + i);
                System.out.println(Thread.currentThread().getName() + "-" + name + "_" + i);
                i++;
                if (ii.size() == 1) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
