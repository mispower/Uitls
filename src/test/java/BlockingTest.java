import java.io.*;

public class BlockingTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        int i = 0;
        File file = new File("D:/1.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter oow = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(oow);
        while (i < 100) {
            bufferedWriter.write("hnihao:" + i);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            i++;
            Thread.sleep(10);
        }
        bufferedWriter.close();
        System.out.println(bufferedWriter.toString());

    }
}
