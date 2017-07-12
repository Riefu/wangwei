import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.reflect.Array.getByte;

/**
 * User: Riefu
 * Date: 2017/7/10
 * Time: 13:33
 */
public class ThreadPullApi implements Runnable {



    private static final List<String> urlList = new ArrayList<String>(Arrays.asList("http://121.28.74.78:8020/HospitalInterface/AddUserInfo?ExtUserID=300001&ExtUserPwd=400001&IDCard=130406198103262124&Phone=18630121163&PatientName=张三&GuardianName=张三妈&Sex=女"));

    private   FileOutputStream fos ;
    private  Logger logger = Logger.getLogger(this.getClass());

    private  Map<String,Map> cacheMap = new HashMap<String, Map>();
    public static void main(String[] args) throws IOException, ClassNotFoundException {

            query();
    }

    public static void query() throws IOException, ClassNotFoundException {


//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//
//        scheduledExecutorService.scheduleWithFixedDelay(new ThreadPullApi(),0,10, TimeUnit.SECONDS);

        ObjectInputStream objectOutputStream = new ObjectInputStream(new FileInputStream(new File("d:/demo.rbd")));
        final Map map = (Map) objectOutputStream.readObject();

        System.out.println("ok");


    }

    @Override
    public void run() {
        // 业务逻辑
        int i = 0;
        final int count = urlList.size();
        for ( ;i<count;i++){
            String url = urlList.get(i);
            // 构造请求
            final OkHttpClient client = new OkHttpClient.Builder().build();
            final Request request = new Request.Builder().url(url).build();

            try {

                final Response response = client.newCall(request).execute();
                final String result = response.body().string();
                final int resultKey = url.hashCode();
                final Map map = JSON.parseObject(result, Map.class);

                cacheMap.put(result,map);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oo = new ObjectOutputStream(bao);
            oo.writeObject(cacheMap);
            final byte[] bytes = bao.toByteArray();
            final FileChannel channel = new FileOutputStream(new File("d:/demo.rbd")).getChannel();
            channel.write(ByteBuffer.wrap(bytes));
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
