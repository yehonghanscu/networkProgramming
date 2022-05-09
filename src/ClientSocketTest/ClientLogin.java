package ClientSocketTest;

import ServerSocketTest.DataPack;
import ServerSocketTest.ServerThread;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * @Author yehonghan
 * @2022/4/13 2:28
 */
public class ClientLogin {
    public static void main(String[] args) {
        System.out.println("-------------登录客户端启动-------------"+ ServerThread.SDF.format(new Date()));
        byte[] loginRequestData;
        Scanner in=new Scanner(System.in);
        try (
                //定义套接口请求通信
                Socket clientsocket =new Socket("127.0.0.1",6666);
                //定义输出流包装打印流发送信息
                OutputStream outputStream =clientsocket.getOutputStream();
                PrintStream printStream=new PrintStream(outputStream);
                //获取字节输入流获取信息
                InputStream inputStream=clientsocket.getInputStream()
        ){
            System.out.println("请输入您的用户名（由数字和字母及_组成）:");
            String name=in.nextLine();
            //用户名校验
            if(checKRegex(name)){
                System.out.println("请输入您的密码（由数字和字母及_组成）:");
                String password=in.nextLine();
                if(checKRegex(password)){
                    //封装数据包并发送到服务端
                    loginRequestData= DataPack.getRequestData(3, name , password);
                    printStream.write(loginRequestData);
                    //处理服务端响应数据
                    byte[] msgBytes=new byte[4];
                    byte[] lenBytes=new byte[4];
                    byte[] flagBytes=new byte[1];
                    byte flagByte;
                    byte[] descriptionBytes=new byte[64];
                    inputStream.read(msgBytes);
                    int ReqMsg=DataPack.byteArrayToInt(msgBytes);
                    inputStream.read(lenBytes);
                    int len=DataPack.byteArrayToInt(lenBytes);
                    inputStream.read(flagBytes);
                    flagByte=flagBytes[0];
                    boolean flag=DataPack.ByteToBoolean(flagByte);
                    inputStream.read(descriptionBytes);
                    String description=DataPack.byteToStr(descriptionBytes);

                    //处理响应信息
                    if(flag){
                        //登录成功打印成功信息
                        System.out.println(description);
                    }else {
                        //登录失败打印出错信息
                        System.err.println(description);
                    }
                }else {
                    System.err.println("您输入的用户名不符合格式");
                }
            }else{
                System.err.println("您输入的用户名不符合格式!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //验证用户名是否合法
    private static boolean checKRegex(String msg) {
        return msg!=null&&msg.matches("\\w{1,20}");
    }
}
