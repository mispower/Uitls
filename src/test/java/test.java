import java.io.*;

public class test {


    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("D:/1.txt");
//        File[] lis = file.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.isFile()&&pathname.canRead()&&pathname.canWrite();
//            }
//        });
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


        char[] b=new char[1024];
        while (true) {

            bufferedReader.read(b,0,1024);
            System.out.println(new String(b));
//           while (bufferedReader.ready()) {
//
//
//               System.out.println(bufferedReader.readLine());
//           }

            //Thread.sleep(1000);
        }

    }
}
