import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class test {


    public static void main(String[] args) throws IOException, InterruptedException {


        List<Integer> ii=new ArrayList<>();
        ii.add(1);
        ii.add(2);
        ii.add(3);
        for (int i:ii){
            ii.remove(i);
        }

//        File file = new File("D:/");
//        while (true) {
//            File[] lis = file.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return pathname.isFile() && pathname.canRead() && pathname.canWrite();
//                }
//            });
//            System.out.println(lis.length);
//            Thread.sleep(1000);
//        }

        AtomicInteger atomicInteger=new AtomicInteger(0);
        atomicInteger.addAndGet(1);
        System.out.println(atomicInteger.get());
//        FileInputStream fileInputStream = new FileInputStream(file);
//        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//
//        char[] b=new char[1024];
//        while (true) {
//
//            bufferedReader.read(b,0,1024);
//            System.out.println(new String(b));
////           while (bufferedReader.ready()) {
////
////
////               System.out.println(bufferedReader.readLine());
////           }
//
//            //Thread.sleep(1000);
//        }

    }
}
