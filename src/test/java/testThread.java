import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.Date;
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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                synchronized (ii) {
                    while (true) {
                        System.out.println(ii.size());
                        System.out.println("ddd:" + threadPool.getActiveCount());
                        ii.add(i + "");
                        ii.add("a");
                        ii.add("b");
                        ii.add("c");
                        ii.add("d");
                        ii.add("e");

                        try {
                            ii.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ii.notifyAll();
                        i++;
                    }
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
            while (true) {
                if (ii.size() <= 0) {
                    try {
                        ii.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    ii.remove(0);
                    System.out.println("remove:" + Thread.currentThread().getName() + "-" + name + "__" + new Date().getTime());
                }

                i++;
                if (ii.size() <= 0)
                    ii.notifyAll();
            }
        }
    }
}
