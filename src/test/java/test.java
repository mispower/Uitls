import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mispower.utils.file.FileAdaptor;
import org.junit.Test;

import java.io.IOException;
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
    public void testFileAdaptor() {


        FileAdaptor fileAdaptor = new FileAdaptor.Builder().setFilePath("d:/1.mp4").build();
        FileAdaptor fileAdaptor1 = new FileAdaptor.Builder().setFilePath("d:/2.mp4").build();
        System.out.println(fileAdaptor.getFileMD5() + ":::" + fileAdaptor1.getFileMD5());

        try {
            fileAdaptor.close();
            fileAdaptor1.close();
        } catch (IOException e) {
            e.printStackTrace();
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
}
