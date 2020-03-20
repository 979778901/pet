package com.wang.pet.util;

import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ShouquanUtil {


    public static JSONObject wechatShouquan(String appid,String secret,String grantType,String shouquanUrl,String code){
        if(StringUtils.isBlank(code)){
            throw new RuntimeException("code 不能为空");
        }
        Map<String,Object> result = new HashMap<>();
        log.info("wxlogin - code: " + code);
        Map<String, Object> param = new HashMap<>();
        param.put("appid", appid);
        param.put("secret", secret);
        param.put("js_code", code);
        param.put("grant_type", grantType);
        String content = null;
        try {
            content = HttpUtil.doGet(shouquanUrl, param);
            JSONObject jsonObject = JSONObject.parseObject(content);
            log.info("微信授权接口返回值{}",jsonObject);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("授权异常");
        }
    }

    public static JSONObject wechatShouquanGetMobile(String appid,String secret,String grantType,String shouquanUrl,String code, String encryptedData, String iv) {
        JSONObject jsonObject = wechatShouquan(appid, secret, grantType, shouquanUrl, code);
        String session_key = jsonObject.getString("session_key");
        return JSONObject.parseObject(getUserInfo(encryptedData,session_key,iv));
    }

    // 算法名
    public static final String KEY_NAME = "AES";
    // 加解密算法/模式/填充方式
    // ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个iv
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * 微信 数据解密<br/>
     * 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充<br/>
     * 对称解密的目标密文:encrypted=Base64_Decode(encryptData)<br/>
     * 对称解密秘钥:key = Base64_Decode(session_key),aeskey是16字节<br/>
     * 对称解密算法初始向量:iv = Base64_Decode(iv),同样是16字节<br/>
     *
     * @param encrypted 目标密文
     * @param session_key 会话ID
     * @param iv 加密算法的初始向量
     */
    public static String wxDecrypt(String encrypted, String session_key, String iv) {
        String json = null;
        byte[] encrypted64 = Base64.decodeBase64(encrypted);
        byte[] key64 = Base64.decodeBase64(session_key);
        byte[] iv64 = Base64.decodeBase64(iv);
        byte[] data;
        try {
            init();
            json = new String(decrypt(encrypted64, key64, generateIV(iv64)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 初始化密钥
     */
    public static void init() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator.getInstance(KEY_NAME).init(128);
    }

    /**
     * 生成iv
     */
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        // Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_NAME);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     * 生成解密
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv)
            throws Exception {
        Key key = new SecretKeySpec(keyBytes, KEY_NAME);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }

    public static String getUserInfo(String encryptedData, String sessionKey, String iv) {
        String result = "";
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base
                        + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters
                    .getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                result = new String(resultByte, StandardCharsets.UTF_8);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String encryptedData = "jAvmGjC/ojqT2A7w7SgHSi/5CRWxck8MIZ6XJMOwgqig8ls9WdzhCGhwLKcrnz6EICe5/3MKY5a5odb5fOW8RvD36D0oQQQt/JTUEp/gX+4zrVmIkyBAHjb9X6IobGKTj9edA7pUbFne9rvihRuJqRGkzjczGUI1ww0qYPpTYVMVdbjxkIR5BAn5DNpD7Ub/zVV2aYhxfmqkDjme//gB/g==";
        String iv = "GlA2r5AU1PONd54/UOwizg==";
        String code = "043vgeOp17VcKl04VpOp1awjOp1vgeOs";
        String appid = "wx198e524778b7d6f2";
        String secret = "6f5d27019d2f263dd9459b0cce5cb6e6";
        String grantType = "authorization_code";
        String shouquanUrl = "https://api.weixin.qq.com/sns/jscode2session";
        System.out.println(wechatShouquanGetMobile(
                appid,
                secret,
                grantType,
                shouquanUrl,
                code,
                encryptedData,
                iv
        ));;
    }

}
