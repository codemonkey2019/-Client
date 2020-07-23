package com.clouddisk.client.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用的数据对象格式，客户端与服务器端通信时使用的数据对象
 * String path: 请求时的请求路径，如果是一个响应则为null
 * String messageBodyJSON: 请求数据或响应数据的JSON字符串表示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBody implements Serializable {
    private String path;
    private String messageBodyJSON;
}
