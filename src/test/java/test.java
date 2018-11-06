import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mispower.utils.file.FileAdaptor;
import com.mispower.utils.file.MultiFiles;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class test {

    private static final int maxPoolSize = Integer.MAX_VALUE;


    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("file-task-%d").build();
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(32, maxPoolSize,
            100000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(64), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    @Test
    public void testFileAdaptor() throws IOException {


        FileAdaptor fileAdaptor = new FileAdaptor.Builder().setFile(new File("d:/1.txt")).build();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fileAdaptor.read(buffer, 0, buffer.length)) != -1) {
            //此处一定要使用指定len,这个buffer覆盖机制有关系，当读取的len<length的时候，
            // 多余位置的数据还是保留上一次的数据并没有清除
            System.out.println(new String(buffer, 0, len));
        }

    }


//System.out.println(fileAdaptor.getSuffix());


    @Test
    public void testAssert() {
        assert 1 == 1;
        System.out.println(1);
        assert 1 == 2 : "类型不正确";
        System.out.println("1==2");
    }

    @Test
    public void testArray() {
        List<String> i = new ArrayList<String>();
        i.add("1");
        i.add("2");
        List<String> t = new ArrayList<>();


        for (int j = 0; j < 3; j++) {
//        t.addAll(i);

            t.addAll(i.subList(0, 2));
            System.out.println(t.get(0) == i.get(0));
            t.set(0, "a");
            //i.clear();
            System.out.println(i + "   " + t);
            t.clear();
        }
    }

    @Test
    public void testMultiFiles() {

        File file = new File("E:\\2");
        long t = System.currentTimeMillis();
        MultiFiles build = new MultiFiles.Builder().setDirPath("E:\\1").build();
        build.monitor();
        long e = System.currentTimeMillis();
        long ee = e - t;
        File[] ff = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        for (File str : ff) {
            boolean deleted = str.delete();

        }
        long e1 = System.currentTimeMillis();
        long ee2 = e1 - e;
        System.out.println("multi :" + ee + "  single:" + ee2);
    }
}
