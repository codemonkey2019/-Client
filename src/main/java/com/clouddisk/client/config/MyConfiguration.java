package com.clouddisk.client.config;

import com.clouddisk.client.crypto.CryptoManager;
import com.cryptotool.block.DIG;
import com.cryptotool.digests.DigestFactory;
import com.cryptotool.digests.MyDigest;
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
