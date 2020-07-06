package com.clouddisk.client.crypto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SMServerKey {
    @Getter
    private byte[] publicKey;
    @Getter
    private byte[] privateKey;
    @Getter
    private byte[] sm4Key;
    @Getter
    private byte[] forwardSearchKey;

}
