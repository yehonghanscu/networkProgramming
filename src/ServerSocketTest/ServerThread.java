package ServerSocketTest;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author yehonghan
 * @2022/4/13 8:50
 */
public class ServerThread {
    public static final String SRC_FILE="./1.properties";
    public static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss EEE a");
    public static ThreadExecutor ThreadPools = new ThreadExecutor(3, 100);
    public static byte[] bytes=new byte[4];

    public static void main(String[] args) {
        System.out.println("---------服务端启动---------"+ ServerThread.SDF.format(new Date()));
        try (
                ServerSocket ListenSocket = new ServerSocket(6666)
        ) {
            while (true) {
                Socket socket = ListenSocket.accept();
                System.out.println("有客户端连接！");
                InputStream inputStream=socket.getInputStream();
                //判断消息类型
                inputStream.read(bytes);
                int requestMsg=DataPack.byteArrayToInt(bytes);

                if(requestMsg==1){
                    //把socket交给注册任务并创建线程
                    ThreadPools.execute(new RegisterClientRunnable(socket));
                }else if(requestMsg==3){
                    //把socket交给登录任务并创建线程
                   ThreadPools.execute(new LoginClientRunnable(socket));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
