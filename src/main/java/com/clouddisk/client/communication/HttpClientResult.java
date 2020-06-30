package com.clouddisk.client.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HttpClientResult implements Serializable {

    public HttpClientResult(int code) {
        this.code = code;
    }

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

}