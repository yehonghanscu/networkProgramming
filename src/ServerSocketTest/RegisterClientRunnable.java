package ServerSocketTest;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @Author yehonghan
 * @2022/4/13 0:07
 */
public class RegisterClientRunnable implements Runnable{
    private Socket socket;

    public RegisterClientRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            //使用字节输入流读入数据
            InputStream inputStream=socket.getInputStream();
            //使用打印流
            OutputStream outputStream=socket.getOutputStream();
            PrintStream printStream=new PrintStream(outputStream)
            ){
            //处理请求数据
            byte[] lenBytes=new byte[4];
            byte[] nameBytes=new byte[20];
            byte[] passwordBytes=new byte[30];
            inputStream.read(lenBytes);
            inputStream.read(nameBytes);
            inputStream.read(passwordBytes);
            int len=DataPack.byteArrayToInt(lenBytes);
            String name=DataPack.byteToStr(nameBytes);
            String password=DataPack.byteToStr(passwordBytes);


            //判断账户文件是否为空
            File file = new File(ServerThread.SRC_FILE);
            //如果文件不存在直接添加响应true
            if(!file.exists()){
                storageID(name, password);
                //发送注册成功响应
                byte[] responseData=DataPack.getResponseData(2, true, "您已经成功注册！");
                printStream.write(responseData);
            }else {
                Set<String> existednames=getName();
                if(existednames.isEmpty()){
                    storageID(name, password);
                    //发送注册成功响应
                    byte[] responseData=DataPack.getResponseData(2, true, "您已经成功注册！");
                    printStream.write(responseData);
                }else {
                    boolean flag=true;
                    for (String existedname : existednames) {
                            if(existedname.equals(name)){
                                flag=false;
                        }
                    }
                    if(flag){
                        storageID(name, password);
                        //发送注册成功响应
                        byte[] responseData=DataPack.getResponseData(2, true, "您已经成功注册！");
                        printStream.write(responseData);
                    }else {
                        //发送注册失败响应
                        byte[] responseData=DataPack.getResponseData(2, false, "注册失败！您的用户名已被占用。");
                        printStream.write(responseData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("有客户端下线！");
        }
    }
    public static void storageID(String name,String password){

        try (
                OutputStream outputStream=new FileOutputStream(ServerThread.SRC_FILE,true)
        ){
            //定义字节流包装属性集存入账户密码
            Properties properties=new Properties();
            //将密码转为hash值存储
            int passwordHashcode=password.hashCode();
            properties.setProperty(name, ""+passwordHashcode);
            properties.store(outputStream,"RegisterTime:"+new Date());
            System.out.println("录入数据成功！");
            outputStream.flush();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Set<String> getName(){
        Set<String> set = null;
        try (
                InputStream inputStream=new FileInputStream(ServerThread.SRC_FILE)
        ){
            Properties properties=new Properties();
            properties.load(inputStream);
            set=properties.stringPropertyNames();//获取键名称的集合
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return set;
        }
    }
}