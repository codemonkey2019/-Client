package com.clouddisk.client.util;


import com.clouddisk.client.crypto.SMServerKey;
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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

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
            byte[] sm4Key = FileUtils.readFile("SMServerKey/sm4.key");
            SecretKey key = toSM4Key(sm4Key);

            byte[] sm2PubKeyByte =  FileUtils.readFile("SMServerKey/ec.x509.pub.der");
            BCECPublicKey sm2PubKey = toSM2PublicKey(sm2PubKeyByte);

            byte[] sm2PriKeyByte = FileUtils.readFile("SMServerKey/ec.pkcs8.pri.der");
            BCECPrivateKey sm2PriKey = toSM2PrivateKey(sm2PriKeyByte);
            return new SMServerKey(sm2PubKey,sm2PriKey,key);
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
            Map<String, byte[]> keyPair = getSM2Key();
            byte[] sm4Key = getSM4Key();
            FileUtils.writeFile("SMServerKey/ec.pkcs8.pri.der", keyPair.get("private"));
            FileUtils.writeFile("SMServerKey/ec.x509.pub.der", keyPair.get("public"));
            FileUtils.writeFile("SMServerKey/sm4.key", sm4Key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
