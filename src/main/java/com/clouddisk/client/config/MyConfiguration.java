package com.clouddisk.client.config;

import com.clouddisk.client.crypto.CryptoManager;
import com.clouddisk.client.util.KeyUtils;
import com.cryptotool.block.AE;
import com.cryptotool.block.DIG;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.cipher.MyCipherFactory;
import com.cryptotool.digests.DigestFactory;
import com.cryptotool.digests.MyDigest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyConfiguration {
    @Bean
    public MyDigest sm3Digist(){
        return DigestFactory.getDigest(DIG.SM3);
    }
    @Bean
    public CryptoManager cryptoManager(){
        return new CryptoManager();
    }
    @Bean
    public BCECPublicKey serverSM2PublicKey(){
        return KeyUtils.getServerSM2PublicKey();
    }
    @Bean
    public MyCipher serverSM2EncryptCipher(@Autowired BCECPublicKey  serverSM2PublicKey){
        return MyCipherFactory.getAECipher(AE.SM2,null,serverSM2PublicKey.getEncoded());
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>()
        );
    }
}
