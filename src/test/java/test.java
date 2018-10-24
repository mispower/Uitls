import com.mispower.utils.file.FileAdaptor;
import org.junit.Test;

public class test {

    @Test
    public void testFileAdaptor() {
        FileAdaptor fileAdaptor = new FileAdaptor.Builder().setSourcePath("D:/1.txt").build();
        System.out.println(fileAdaptor.getSuffix());
    }

    @Test
    public void testAssert(){
        assert 1==1;
        System.out.println(1);
        assert 1==2:"类型不正确";
        System.out.println("1==2");
    }
}
