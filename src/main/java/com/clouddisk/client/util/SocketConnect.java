package com.clouddisk.client.util;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class SocketConnect {
    public static void sendMessageBodyToServer(Socket socket, MessageBody messageBody){
        BufferedWriter bw;
        try {
            bw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(JSON.toJSONString(messageBody));
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MessageBody getMessageBodyFromServer(Socket socket){
        BufferedReader br;
        MessageBody messageBody=new MessageBody();
        try {
            br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data = br.readLine();
            messageBody=JSON.parseObject(data,MessageBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageBody;
    }

    public static MessageBody request(Socket socket,MessageBody request){
        sendMessageBodyToServer(socket,request);
        return getMessageBodyFromServer(socket);
    }
    @NotNull
    public static Socket connectToServer(){
        Socket socket=null;
        try {
            socket= new Socket("localhost", 8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }
}
