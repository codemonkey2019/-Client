package com.clouddisk.client.crypto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import javax.crypto.SecretKey;

@AllArgsConstructor
@NoArgsConstructor
public class SMServerKey {
    @Getter
    private BCECPublicKey publicKey;
    @Getter
    private BCECPrivateKey privateKey;
    @Getter
    private SecretKey sm4Key;
    @Getter
    private byte[] forwardSearchKey;

}
