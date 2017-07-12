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

import static java.util.Arrays.asList;

/**
 * User: Riefu
 * Date: 2017/7/10
 * Time: 13:33
 */
public class ThreadPullApi implements Runnable {



    private static final List<String> urlList = new ArrayList<String>();

    static {
        asList("","");//  待访问API地址
    }
    private   FileOutputStream fos ;

    private  Map<String,Map> cacheMap = new HashMap<String, Map>(); //需要缓存的查询结果 已二进制数据缓存至本地文件


    /**
     * 主线程
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {

            query();
    }

    public static void query() throws IOException, ClassNotFoundException {


        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(); //构造线程池

        scheduledExecutorService.scheduleWithFixedDelay(new ThreadPullApi(),0,5, TimeUnit.SECONDS);// 立即执行  线程执行结束后等待5s继续执行


    }

    @Override
    public void run() {
        // 业务逻辑
        int i = 0;
        final int count = urlList.size(); // api列表长度
        for ( ;i<count;i++){
            String url = urlList.get(i);
            // 构造请求
            final OkHttpClient client = new OkHttpClient.Builder().build();
            final Request request = new Request.Builder().url(url).build();

            try {

                final Response response = client.newCall(request).execute(); //获取响应
                final String result = response.body().string();
                final int resultKey = url.hashCode();//每个Result的HashCode作为key对应缓存结果
                final Map map = JSON.parseObject(result, Map.class); //响应反序列化为java对象

                cacheMap.put(result,map);//缓存结果集

            } catch (IOException e) {
                e.printStackTrace();// 请使用logger缓存
            }
        }

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oo = new ObjectOutputStream(bao);
            oo.writeObject(cacheMap);
            final byte[] bytes = bao.toByteArray();
            final FileChannel channel = new FileOutputStream(new File("d:/demo.rbd")).getChannel();//缓存文件路径  并非aof方式追加进缓存文件每次都是最新的缓存数据
            channel.write(ByteBuffer.wrap(bytes));//写进文件
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
