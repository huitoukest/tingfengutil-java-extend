package com.tingfeng.util.java.extend.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

/**
 * RSA 工具类
 */
public class RSAUtils {
    /**
     * 算法/模式/补码方式
     */
    private static String TRANSTYPE = "RSA/ECB/PKCS1Padding";

    /**
     * 获取私钥Base64编码字符串
     * @throws UnsupportedEncodingException
     */
    public static String getPrivateKeyByBase64(RSAPrivateKey privateKey) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(privateKey.getEncoded()), "UTF-8");
    }


    /**
     * 获取公钥Base64编码字符串
     * @throws UnsupportedEncodingException
     */
    public static String getPublicKeyByBase64(RSAPublicKey publicKey) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(publicKey.getEncoded()), "UTF-8");
    }

    /**
     * 随机生成密钥对
     * @throws Exception
     */
    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("算法错误", e);
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
       return keyPair;
    }

    /**
     * 从文件中输入流中加载公钥
     * @throws Exception
     *
     */
    public static RSAPublicKey loadPublicKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            return getPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误", e);
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空", e);
        }
    }

    /**
     * 从字符串中加载公钥
     * @throws Exception
     *
     */
    public static RSAPublicKey getPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法", e);
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空", e);
        }
    }

    /**
     * 从文件中加载私钥
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            return getPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误", e);
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空", e);
        }
    }

    public static RSAPrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法", e);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new Exception("私钥非法", e);
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空", e);
        }
    }

    /**
     * 加密过程
     *
     * @param publicKey
     *            公钥
     * @param plainTextData
     *            明文数据
     * @return
     * @throws Exception
     *
     */
    public static String encrypt(String plainTextData,RSAPublicKey publicKey) throws Exception {
        if (publicKey == null) {
            throw new Exception("公钥为空");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData.getBytes("UTF-8"));
            return new String(Base64.encodeBase64(output), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法", e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法", e);
        } catch (BadPaddingException e) {
            throw new Exception("明文数据损坏", e);
        }
    }

    /**
     * 解密过程
     *
     * @param privateKey
     *            私钥
     * @param cipherData
     *            密文数据
     * @return 明文
     * @throws Exception
     *
     */
    public static String decrypt(String cipherData,RSAPrivateKey privateKey) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空,");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSTYPE);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(Base64.decodeBase64(cipherData.getBytes("UTF-8")));
            return new String(output, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法", e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法", e);
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏", e);
        }
    }


    /**
     * 数据签名
     *
     * @param privateKey
     *            私钥
     * @param plainTextData
     *            明文数据
     * @return 加密数据
     * @throws Exception
     *
     */
    public static String sign(String plainTextData,RSAPrivateKey privateKey) throws Exception {
        if (privateKey == null) {
            throw new Exception("签名私钥为空");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(plainTextData.getBytes("UTF-8"));
            return new String(Base64.encodeBase64(output), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法", e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("签名私钥非法", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法", e);
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏", e);
        }
    }

    /**
     * 签名校验
     * @param cipherData
     * @param publicKey
     * @return 解密数据
     * @throws Exception 校验失败会抛出异常
     */
    public static String verifySign(String cipherData,RSAPublicKey publicKey) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSTYPE);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(Base64.decodeBase64(cipherData.getBytes()));
            return new String(output, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法", e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法", e);
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏", e);
        }
    }
}
