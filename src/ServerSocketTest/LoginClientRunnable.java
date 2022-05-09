package ServerSocketTest;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Set;

/**
 * @Author yehonghan
 * @2022/4/13 2:32
 */
public class LoginClientRunnable implements Runnable{
    private Socket socket;

    public LoginClientRunnable(Socket socket) {
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
                //发送登录失败响应
                byte[] responseData=DataPack.getResponseData(4, false, "登录失败！您的用户名不存在！");
                printStream.write(responseData);
            }else {
                //查找所有已存在用户名
                Set<String> existednames=getName();
                boolean isexistName=false;
                for (String existedname : existednames) {
                    if(existedname.equals(name)){
                        isexistName=true;
                    }
                }
                if(isexistName){
                    String namePassword=getPassword(name);
                    //获取对应密码hash值并转为字符串
                    String namePasswordHashcode=""+password.hashCode();
                    if(namePassword.equals(namePasswordHashcode)){
                        byte[] responseData=DataPack.getResponseData(4, true, "登录成功！");
                        printStream.write(responseData);
                    }else {
                        //发送登录失败响应
                        byte[] responseData=DataPack.getResponseData(4, false, "登录失败！您的密码错误！");
                        printStream.write(responseData);
                    }
                }else {
                    //发送登录失败响应
                    byte[] responseData=DataPack.getResponseData(4, false, "登录失败！您的用户名不存在！");
                    printStream.write(responseData);
                }
            }

        } catch (Exception e) {
                e.printStackTrace();
        }finally {
            System.out.println("有登录客户端下线！");
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
    public static String getPassword(String name){
        String password = null;
        try (
                InputStream inputStream=new FileInputStream(ServerThread.SRC_FILE)
        ){
            Properties properties=new Properties();
            properties.load(inputStream);
            password=properties.getProperty(name);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return password;
        }
    }
}

