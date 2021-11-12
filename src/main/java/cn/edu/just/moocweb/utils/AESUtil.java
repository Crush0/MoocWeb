package cn.edu.just.moocweb.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @program: test
 * @description: AES 加密解密工具类
 * @author: 闲走天涯
 * @create: 2021-08-20 15:58
 */
public class AESUtil {

    //秘钥
    public static final String SECRET_KEY = "wdesjqiw123oehs12sn32nv325w6q23vdf9g6h";
    //秘钥位数，限制秘钥长度为 16/32
    private static final Integer DIGIT = 32;

    /**
     * 加密
     * 1.构造密钥生成器
     * 2.根据ecnodeRules规则初始化密钥生成器
     * 3.产生密钥
     * 4.创建和初始化密码器
     * 5.内容加密
     * 6.返回字符串
     * @param encodeRules 秘钥
     * @param content 内容
     * @return
     */
    public static String AESEncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes(StandardCharsets.UTF_8);
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            //11.将字符串返回
            return new BASE64Encoder().encode(byte_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密 第二种方式
     * @param encodeRules 秘钥
     * @param content 内容
     * @return
     */
    public static String AESEncode2(String encodeRules,String content){
        try {
            //拼接秘钥位数16/32
            encodeRules=getKeyByDigit(encodeRules,DIGIT);
            //生成AES密钥
            SecretKey key=new SecretKeySpec(encodeRules.getBytes(), "AES");
            //根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes(StandardCharsets.UTF_8);
            //根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(byte_encode);
            //将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            //将字符串返回
            return new BASE64Encoder().encode(byte_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串反纺成byte[]数组
     * 3.将加密内容解密
     * @param encodeRules 秘钥
     * @param encodeContent 密文
     * @return
     */
    public static String AESDncode(String encodeRules,String encodeContent){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //8.将加密并编码后的内容解码成字节数组
            byte [] byte_content= new BASE64Decoder().decodeBuffer(encodeContent);
            //解密
            byte [] byte_decode=cipher.doFinal(byte_content);
            //输出字符串
            return new String(byte_decode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加null
        return null;
    }

    /**
     * 解密 第二种方式
     * @param encodeRules 秘钥
     * @param encodeContent 密文
     * @return
     */
    public static String AESDncode2(String encodeRules,String encodeContent){
        try {
            //拼接秘钥位数16/32
            encodeRules=getKeyByDigit(encodeRules,DIGIT);
            SecretKey key=new SecretKeySpec(encodeRules.getBytes(), "AES");
            //根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //将加密并编码后的内容解码成字节数组
            byte [] byte_content= new BASE64Decoder().decodeBuffer(encodeContent);
            //解密
            byte [] byte_decode=cipher.doFinal(byte_content);
            //输出字符串
            return new String(byte_decode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 控制密钥位数为16/32位
     * @param key
     * @return
     */
    public static String getKeyByDigit(String key,Integer digit){
        StringBuilder newKey = new StringBuilder();
        if(key.length()>digit){
            newKey.append(key.substring(0,digit));
        }else if(key.length()<digit){
            newKey.append(key);
            for(int i=0;i<digit-key.length();i++){
                newKey.append("1");
            }
        }else{
            newKey.append(key);
        }
        return newKey.toString();
    }

//    public static void main(String[] args) {
//        String content = "你好,中国";
//        //加密
//        String encode = AESUtil.AESEncode(SECRET_KEY,content);
//        //解密
//        String dncode = AESUtil.AESDncode(SECRET_KEY,encode);
//        System.out.println("原文："+content);
//        System.out.println("加密后："+encode);
//        System.out.println("解密后："+dncode);
//        System.out.println("秘钥位数："+SECRET_KEY.length()+",密文位数："+encode.length());
//    }
}
