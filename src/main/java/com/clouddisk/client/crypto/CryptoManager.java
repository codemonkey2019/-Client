package com.clouddisk.client.crypto;

import com.cryptotool.block.AE;
import com.cryptotool.block.Padding;
import com.cryptotool.block.Pattern;
import com.cryptotool.block.SE;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.cipher.MyCipherFactory;
import lombok.Getter;

/**
 * 管理使用到的所有的密码学工具对象，它的一个实例会被注册到spring容器中
 */
@Getter
public class CryptoManager {
    private MyCipher sm2Cipher;
    private MyCipher sm4Cipher;
    private SMKeys smServerKey;

    public void init(SMKeys smServerKey){
        this.smServerKey=smServerKey;
        sm2Cipher = MyCipherFactory.getAECipher(AE.SM2
                ,smServerKey.getPrivateKey()
                ,smServerKey.getPublicKey());
        sm4Cipher= MyCipherFactory.getSECipher(SE.SM4, Pattern.CBC, Padding.PKCS5,smServerKey.getSm4Key());
    }
    public MyCipher cloneSM4Cipher(){
        return sm4Cipher= MyCipherFactory.getSECipher(SE.SM4, Pattern.CBC, Padding.PKCS5,smServerKey.getSm4Key());
    }
}
