package com.clouddisk.client.crypto;

import com.cryptotool.block.AE;
import com.cryptotool.block.Padding;
import com.cryptotool.block.Pattern;
import com.cryptotool.block.SE;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.cipher.MyCipherFactory;
import lombok.Getter;

@Getter
public class CryptoManager {
    private MyCipher sm2Cipher;
    private MyCipher sm4Cipher;
    private SMServerKey smServerKey;

    public void init(SMServerKey smServerKey){
        this.smServerKey=smServerKey;
        sm2Cipher = MyCipherFactory.getAECipher(AE.SM2
                ,smServerKey.getPrivateKey()
                ,smServerKey.getPublicKey());
        sm4Cipher= MyCipherFactory.getSECipher(SE.SM4, Pattern.CBC, Padding.PKCS5,smServerKey.getSm4Key());
    }
}
