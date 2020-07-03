package com.clouddisk.client.util;


import com.clouddisk.client.crypto.SMServerKey;
import com.cryptotool.block.DIG;
import com.cryptotool.digests.DigestFactory;
import com.cryptotool.util.BCECUtil;
import com.cryptotool.util.SM2Util;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import javax.crypto.KeyGenerator;
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

    public static SMServerKey getSMServerKeyFromFile(){
        try {
            byte[] sm4Key = FileUtils.readFile("C:/MyCloudDisk/SMServerKey/sm4.key");
            SecretKey key = toSM4Key(sm4Key);

            byte[] sm2PubKeyByte =  FileUtils.readFile("C:/MyCloudDisk/SMServerKey/ec.x509.pub.der");
            BCECPublicKey sm2PubKey = toSM2PublicKey(sm2PubKeyByte);

            byte[] sm2PriKeyByte = FileUtils.readFile("C:/MyCloudDisk/SMServerKey/ec.pkcs8.pri.der");
            BCECPrivateKey sm2PriKey = toSM2PrivateKey(sm2PriKeyByte);
            byte[] forwardSearchKey = FileUtils.readFile("C:/MyCloudDisk/SMServerKey/forwardSearchKey.key");
            return new SMServerKey(sm2PubKey,sm2PriKey,key,forwardSearchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SMServerKey();
    }

    private static byte[] getSM4Key(){

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("SM4", "BC");
            return keyGenerator.generateKey().getEncoded();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return new byte[16];
    }

    private static SecretKey toSM4Key(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "SM4");
        return secretKey;
    }

    private static  Map<String, byte[]> getSM2Key(){
        Map<String, byte[]> out = new HashMap<>();
        AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
        ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();
        byte[] priKeyPkcs8Der = BCECUtil.convertECPrivateKeyToPKCS8(priKey, pubKey);
        out.put("private",priKeyPkcs8Der);
        byte[] pubKeyX509Der = BCECUtil.convertECPublicKeyToX509(pubKey);
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

    public static void genSMServerKeyToFile() {
        try {
            File file = new File("C:/MyCloudDisk/SMServerKey/");
            if (!file.exists()||!file.isDirectory()){
                file.mkdir();
            }
            Map<String, byte[]> keyPair = getSM2Key();
            byte[] sm4Key = getSM4Key();
            byte[] forwardSearchKey = DigestFactory.getDigest(DIG.SM3).getDigest(new Random().nextInt(100)+"").getBytes();
            FileUtils.writeFile("C:/MyCloudDisk/SMServerKey/ec.pkcs8.pri.der", keyPair.get("private"));
            FileUtils.writeFile("C:/MyCloudDisk/SMServerKey/ec.x509.pub.der", keyPair.get("public"));
            FileUtils.writeFile("C:/MyCloudDisk/SMServerKey/sm4.key", sm4Key);
            FileUtils.writeFile("C:/MyCloudDisk/SMServerKey/forwardSearchKey.key",forwardSearchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
