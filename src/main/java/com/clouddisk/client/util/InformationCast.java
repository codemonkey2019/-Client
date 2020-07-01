package com.clouddisk.client.util;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;

public class InformationCast {

    public static <T> T messageBodyToReqponseBody(MessageBody messageBody, Class<T> clazz){
        return JSON.parseObject(messageBody.getMessageBodyJSON(), clazz);
    }
    public static String objectToJson(Object obj){
        return JSON.toJSONString(obj);
    }
}
