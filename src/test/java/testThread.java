import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class testThread implements Runnable {
    private String name;

    testThread(String name) {
        this.name = name;
    }

    private static final int maxPoolSize = Integer.MAX_VALUE;
    private static final int concurrencyCount = 32;

    static List<String> ii = new ArrayList<>();
    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().build();
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(concurrencyCount, maxPoolSize,
            100000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    private static Queue[] queue;

    private static int maxLength = 3;
    private static int thread_num = 10;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                doProducer();
            }
        });

        t.start();
        //     t.join();
        List<Runnable> tasks = new ArrayList<>();
        System.out.println("start thread:" + thread_num);
        for (int i = 0; i < thread_num; i++) {
            tasks.add(new testThread(i + ""));
        }
        for (Runnable a : tasks) {
            threadPool.execute(a);
        }
        System.out.println("active count:" + threadPool.getActiveCount());

    }

    private static void doProducer() {
        queue = new LinkedBlockingQueue[12];
        File file = new File("D:/exportdata");
        int index = 0;
        while (index < 1) {
            System.out.println("producer:" + index);
//            ii.add("a:" + index);
//            ii.add("b:" + index);
//            ii.add("c:" + index);
//            ii.add("d:" + index);
//            ii.add("e:" + index);
//            ii.add("f:" + index);
//            ii.add("g:" + index);

            ii.addAll(Arrays.asList(file.list()));
            System.out.println("file count:" + file.list().length);

            int length = ii.size();
            maxLength = length / thread_num + 1;
            if (length > 0) {
                synchronized (queue) {
                    for (int j = 0; j < thread_num; j++) {
                        if (queue[j] == null) {
                            queue[j] = new LinkedBlockingQueue();
                        }
                        int len = maxLength;
                        int start = j * maxLength;
                        len += start;
                        if (len > length) {
                            len = length;
                        }
                        if (start <= length)
                            queue[j].addAll(ii.subList(start, len));
//                        System.out.println("数据：" + j + ":" + queue[j]);
                    }
                    //分配好数据，唤醒其他线程
                    queue.notifyAll();
                    //释放锁
                    ii.clear();
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


//            if (index == 9) {
//                try {
//                    Thread.sleep(100000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            index++;
        }

    }


    @Override
    public void run() {
        int index = Integer.valueOf(name);
        //copy source  data to target to
        Queue target = new LinkedBlockingQueue();
        Queue tmp;
        while (true) {
            synchronized (queue) {
                tmp = queue[index];
                if (tmp == null) continue;
                if (tmp.size() <= 0) {
                    try {
//                        System.out.println("wait:" + name);
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                target.addAll(tmp);
                tmp.clear();
                //唤醒其他线程，释放当前锁
                queue.notifyAll();
//                System.out.println("notify:" + name);
            }

            //logic code
            while (target.size() > 0) {
                int filecout = 0;
                Object o = target.poll();
                File file = new File("D:/exportdata/" + o.toString());

                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    while (bufferedReader.ready()) {
                        if (bufferedReader.readLine() != null) {
                            filecout++;
                            atomicInteger.addAndGet(1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println("remove ," + name );

            }

        }
    }


}
