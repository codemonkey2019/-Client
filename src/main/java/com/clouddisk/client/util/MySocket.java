package com.clouddisk.client.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.Socket;

/**
 * 本次连接的Socket的封装
 */
@Component
public class MySocket {
    @Getter
    @Setter
    private Socket socket;
}
