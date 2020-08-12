package com.clouddisk.client.crypto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 密钥集合对象，客户端启动时会根据输入的信息生成对应的密钥集合对象
 */
@AllArgsConstructor
@NoArgsConstructor
public class SMKeys {
    @Getter
    private byte[] publicKey;
    @Getter
    private byte[] privateKey;
    @Getter
    private byte[] sm4Key;
    @Getter
    private byte[] forwardSearchKey;

}
