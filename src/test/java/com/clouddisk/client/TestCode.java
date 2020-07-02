package com.clouddisk.client;

import com.cryptotool.block.Padding;
import com.cryptotool.block.Pattern;
import com.cryptotool.block.SE;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.cipher.MyCipherFactory;
import com.cryptotool.util.KeyUtils;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class TestCode {
    @Test
    public void testCode(){
        String a = "aaaaaaadasrfasfsgaerASGDFHSZERTZDHXZDRYXDHdfxuxftyxfhxdfgdf";
        byte[] key = KeyUtils.getSecretKey(SE.SM4);
        MyCipher cipher = MyCipherFactory.getSECipher(SE.SM4, Pattern.CBC, Padding.PKCS5,key);
        byte[] aa = cipher.encrypt(a.getBytes());
        System.out.println(Base64.getEncoder().encodeToString(aa));
    }
    @Test
    public void test(){
        System.out.println(System.getProperty("user.dir"));
    }
}
