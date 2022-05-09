package ServerSocketTest;


/**
 * @Author yehonghan
 * @2022/4/20 13:48
 */
public class DataPack {

    //包装请求包
    public static byte[] getRequestData(int commandID, String name,String password){
        int ReqMsg=commandID;
        int totallen=58;
        int namelen=20;
        int passwordlen=30;
        byte[] msgBytes=intToBytes(ReqMsg);
        byte[] lenBytes=intToBytes(totallen);
        byte[] nameBytes=getStringBytes(name, namelen);
        byte[] passwordBytes=getStringBytes(password, passwordlen);
        byte[] ReqBytes=new byte[totallen];
        System.arraycopy(msgBytes, 0, ReqBytes, 0, msgBytes.length);
        System.arraycopy(lenBytes, 0, ReqBytes,msgBytes.length, lenBytes.length);
        System.arraycopy(nameBytes, 0, ReqBytes, msgBytes.length+lenBytes.length, nameBytes.length);
        System.arraycopy(passwordBytes, 0, ReqBytes, msgBytes.length+lenBytes.length+nameBytes.length, passwordBytes.length);
        return ReqBytes;
    }
    //包装响应包
    public static byte[] getResponseData(int commandID,boolean status,String description){
        int RespMsg=commandID;
        int totallen=73;
        boolean flag=status;
        int descriptionlen=64;
        byte[] msgBytes=intToBytes(RespMsg);
        byte[] lenBytes=intToBytes(totallen);
        byte flagByte=BooleanToByte(flag);
        byte[] flagBytes=new byte[1];
        flagBytes[0]=flagByte;
        byte[] descriptionBytes=getStringBytes(description,descriptionlen);
        byte[] RespBytes=new byte[totallen];
        System.arraycopy(msgBytes, 0, RespBytes, 0, msgBytes.length);
        System.arraycopy(lenBytes, 0, RespBytes,msgBytes.length, lenBytes.length);
        System.arraycopy(flagBytes, 0, RespBytes, msgBytes.length+lenBytes.length, flagBytes.length);
        System.arraycopy(descriptionBytes, 0, RespBytes, msgBytes.length+lenBytes.length+flagBytes.length, descriptionBytes.length);
        return RespBytes;
    }

    //boolean转换为byte
    public static byte BooleanToByte(boolean flag) {
        byte result = (byte)(0);
        if(flag){
            result=(byte)(1 & 0xFF);
        }
        return result;
    }

    //byte转换为boolean
    public static boolean ByteToBoolean(byte data) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(data & 0xFF) << shift;
        }
        if(value==0){
            return false;
        }
        return true;
    }


    //字符串转换为byte数组
    public static byte[] getStringBytes(String s, int length){
        int fixLength = length - s.getBytes().length;
        byte[] sBytes = new byte[length];
        if (s.getBytes().length < length) {
            System.arraycopy(s.getBytes(), 0, sBytes, 0, s.getBytes().length);
            for (int x = length-fixLength; x < length; x++) {
                sBytes[x] = 0x00;
            }
            return sBytes;
        }
        return s.getBytes();
    }

    //去掉byte[]中填充的0 转为String
    public static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    //int类型转换为byte数组
    public static byte[] intToBytes(int value )
    {
        byte[] result = new byte[4];
        result[0] = (byte)((value >> 24) & 0xFF);
        result[1] = (byte)((value >> 16) & 0xFF);
        result[2] = (byte)((value >> 8) & 0xFF);
        result[3] = (byte)(value & 0xFF);
        return result;
    }
    //byte数组转换为int
    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }
}
