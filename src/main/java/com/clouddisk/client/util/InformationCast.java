package com.clouddisk.client.util;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;

/**
 * 转换消息对象MessageBody到对应的具体的数据对象
 */
public class InformationCast {

    public static <T> T messageBodyToReqponseBody(MessageBody messageBody, Class<T> clazz){
        return JSON.parseObject(messageBody.getMessageBodyJSON(), clazz);
    }
}
