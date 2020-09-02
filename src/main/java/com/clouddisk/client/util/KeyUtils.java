package com.clouddisk.client.util;


import com.clouddisk.client.crypto.SMKeys;
import com.cryptotool.block.AE;
import com.cryptotool.block.DIG;
import com.cryptotool.block.SE;
import com.cryptotool.cipher.asymmetric.AEKeyPair;
import com.cryptotool.digests.DigestFactory;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

/**
 * 密钥管理的工具类
 * @author L
 * @date 2019-09-20 21:37
 * @desc
 **/
@Slf4j
public class KeyUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
//
//    public static SMServerKey getSMServerKeyByNameFromFile(String userName){
//        String basePath = "C:/MyCloudDisk/"+userName+"/SMServerKey/";
//        try {
//            byte[] sm4Key = FileUtils.readFile(basePath+"sm4.key");
//
//            byte[] sm2PubKeyByte =  FileUtils.readFile(basePath+"ec.x509.pub.der");
//
//            byte[] sm2PriKeyByte = FileUtils.readFile(basePath+"ec.pkcs8.pri.der");
//            byte[] forwardSearchKey = FileUtils.readFile(basePath+"forwardSearchKey.key");
//            return new SMServerKey(sm2PubKeyByte,sm2PriKeyByte,sm4Key,forwardSearchKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new SMServerKey();
//    }

    private static byte[] getSM4Key(byte[] seed){

        byte[] secretKeyBySeed = com.cryptotool.util.KeyUtils.getSecretKeyBySeed(SE.SM4, seed);
        return secretKeyBySeed;
    }

    public static BCECPublicKey getServerSM2PublicKey(){
        BCECPublicKey bcecPublicKey = null;
        try {
            byte[] key = FileUtils.readFile("SM2KeyPair\\ec.x509.pub.der");
            bcecPublicKey= com.cryptotool.util.KeyUtils.toSM2PublicKey(key);
        } catch (IOException e) {
            log.error("服务器端密钥文件读取失败");
            e.printStackTrace();
        }
        return bcecPublicKey;
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

    public static SMKeys genSMServerKey(byte[] seed) {

            Map<String, byte[]> keyPair = getSM2Key(seed);
            byte[] sm4Key = getSM4Key(seed);
            byte[] forwardSearchKey = DigestFactory.getDigest(DIG.SM3).getDigest(seed);
            return new SMKeys(keyPair.get("public"),keyPair.get("private"),sm4Key,forwardSearchKey);
    }

//    public static void genSMServerKeyByUserNameToFile(String userName, byte[] seed) {
//        String basePath = "C:/MyCloudDisk/"+userName+"/SMServerKey/";
//        try {
//            File file = new File(basePath);
//            if (!file.exists()||!file.isDirectory()){
//                file.mkdirs();
//            }
//            Map<String, byte[]> keyPair = getSM2Key(seed);
//            byte[] sm4Key = getSM4Key(seed);
//            byte[] forwardSearchKey = DigestFactory.getDigest(DIG.SM3).getDigest(new Random().nextInt(100)+"").getBytes();
//            FileUtils.writeFile(basePath+"ec.pkcs8.pri.der", keyPair.get("private"));
//            FileUtils.writeFile(basePath+"ec.x509.pub.der", keyPair.get("public"));
//            FileUtils.writeFile(basePath+"sm4.key", sm4Key);
//            FileUtils.writeFile(basePath+"forwardSearchKey.key",forwardSearchKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
