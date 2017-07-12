import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * User: Riefu
 * Date: 2017/7/12
 * Time: 10:25
 */
public class ApiUnSerliaze {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(new File("d:/deme.rbd"));

        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        final Map map = (Map) objectInputStream.readObject();
        
    }

}
