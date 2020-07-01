package com.clouddisk.client.config;

import com.clouddisk.client.crypto.SMServerKey;
import com.clouddisk.client.util.KeyUtils;
import com.cryptotool.block.*;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.cipher.MyCipherFactory;
import com.cryptotool.digests.DigestFactory;
import com.cryptotool.digests.MyDigest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class MyConfiguration {

    @Bean
    public SMServerKey smServerKey(){
        File f1 = new File("C:/MyCloudDisk/SMServerKey/ec.pkcs8.pri.der");
        File f2 = new File("C:/MyCloudDisk/SMServerKey/ec.x509.pub.der");
        File f3 = new File("C:/MyCloudDisk/SMServerKey/sm4.key");
        File f4 = new File("C:/MyCloudDisk/SMServerKey/forwardSearchKey.key");
        if (!f1.exists()||!f2.exists()||!f3.exists()||!f4.exists()){
            KeyUtils.genSMServerKeyToFile();
        }
        return KeyUtils.getSMServerKeyFromFile();
    }
    @Bean
    public MyCipher sm2Cipher( SMServerKey smServerKey){
        MyCipher sm2Cipher = MyCipherFactory.getAECipher(AE.SM2
                ,smServerKey().getPrivateKey().getEncoded()
                ,smServerKey.getPublicKey().getEncoded());
        return sm2Cipher;
    }
    @Bean
    public MyCipher sm4Cipher( SMServerKey smServerKey){
        return MyCipherFactory.getSECipher(SE.SM4, Pattern.CBC, Padding.PKCS5,smServerKey.getSm4Key().getEncoded());
    }
    @Bean
    public MyDigest sm3Digist(){
        return DigestFactory.getDigest(DIG.SM3);
    }
}
