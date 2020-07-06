package com.clouddisk.client.util;


import com.clouddisk.client.crypto.SMServerKey;
import com.cryptotool.block.AE;
import com.cryptotool.block.DIG;
import com.cryptotool.block.SE;
import com.cryptotool.cipher.asymmetric.AEKeyPair;
import com.cryptotool.digests.DigestFactory;
import com.cryptotool.util.BCECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author L
 * @date 2019-09-20 21:37
 * @desc
 **/
public class KeyUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static SMServerKey getSMServerKeyByNameFromFile(String userName){
        String basePath = "C:/MyCloudDisk/"+userName+"/SMServerKey/";
        try {
            byte[] sm4Key = FileUtils.readFile(basePath+"sm4.key");

            byte[] sm2PubKeyByte =  FileUtils.readFile(basePath+"ec.x509.pub.der");

            byte[] sm2PriKeyByte = FileUtils.readFile(basePath+"ec.pkcs8.pri.der");
            byte[] forwardSearchKey = FileUtils.readFile(basePath+"forwardSearchKey.key");
            return new SMServerKey(sm2PubKeyByte,sm2PriKeyByte,sm4Key,forwardSearchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SMServerKey();
    }

    private static byte[] getSM4Key(byte[] seed){

        byte[] secretKeyBySeed = com.cryptotool.util.KeyUtils.getSecretKeyBySeed(SE.SM4, seed);
        return secretKeyBySeed;
    }

    private static SecretKey toSM4Key(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "SM4");
        return secretKey;
    }

    private static  Map<String, byte[]> getSM2Key(byte[] seed){
        Map<String, byte[]> out = new HashMap<>();
        AEKeyPair aeKeyPairBySeed = com.cryptotool.util.KeyUtils.getAEKeyPairBySeed(AE.SM2, seed);
        byte[] priKeyPkcs8Der = aeKeyPairBySeed.getPrivateKey();
        out.put("private",priKeyPkcs8Der);
        byte[] pubKeyX509Der = aeKeyPairBySeed.getPublicKey();
        out.put("public",pubKeyX509Der);
        return out;
    }

    /**
     * 转化SM2的公钥
     * @param publicKey
     * @return
     */
    @NotNull
    private static BCECPublicKey toSM2PublicKey(byte[] publicKey){
        if(publicKey==null){

            return null;
        }
        try {
            BCECPublicKey sm2PubKey = BCECUtil.convertX509ToECPublicKey(publicKey);
            return sm2PubKey;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换SM2的私钥
     * @param privateKey
     * @return
     */
    @NotNull
    private static BCECPrivateKey toSM2PrivateKey(byte[] privateKey){
        if(privateKey==null){
            return null;
        }
        try {
            BCECPrivateKey sm2PriKey = BCECUtil.convertPKCS8ToECPrivateKey(privateKey);
            return sm2PriKey;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void genSMServerKeyByUserNameToFile(String userName, byte[] seed) {
        String basePath = "C:/MyCloudDisk/"+userName+"/SMServerKey/";
        try {
            File file = new File(basePath);
            if (!file.exists()||!file.isDirectory()){
                file.mkdirs();
            }
            Map<String, byte[]> keyPair = getSM2Key(seed);
            byte[] sm4Key = getSM4Key(seed);
            byte[] forwardSearchKey = DigestFactory.getDigest(DIG.SM3).getDigest(new Random().nextInt(100)+"").getBytes();
            FileUtils.writeFile(basePath+"ec.pkcs8.pri.der", keyPair.get("private"));
            FileUtils.writeFile(basePath+"ec.x509.pub.der", keyPair.get("public"));
            FileUtils.writeFile(basePath+"sm4.key", sm4Key);
            FileUtils.writeFile(basePath+"forwardSearchKey.key",forwardSearchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
