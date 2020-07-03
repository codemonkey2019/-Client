package com.clouddisk.client.util;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class SocketConnect {

    public static boolean sendFileToServer(Socket socket, String filePath, StringProperty percentage){
        boolean success = false;
        File sendFile = new File(filePath);
        long length = sendFile.length();
        if (sendFile.exists()) {
            BufferedOutputStream bos=null;
            BufferedInputStream bis=null;
            DataOutputStream dos =null;
            try {
                bos = new BufferedOutputStream(socket.getOutputStream());
                bis = new BufferedInputStream(new FileInputStream(sendFile));
                dos= new DataOutputStream(bos);
                //发送文件长度
                dos.writeLong(sendFile.length());
                dos.flush();
                byte[] b = new byte[1024];
                int len = 0;
                long sum = 0L;
                while ((len=bis.read(b))!=-1){
                    bos.write(b,0,len);
                    //返回进度
                    sum+=len;
                    double part =(sum/(length+0.0))*100;
                    percentage.setValue(part+"");
                }
                bos.flush();
                success=true;
            } catch (IOException e) {
                try {
                    bos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }finally {
                if (bis!=null){
                    try {
                        bis.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return success;
    }

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
    public static Socket connectToServer(String ip){
        Socket socket=null;
        try {
            socket= new Socket(ip, 8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static String douwnloadFile(String rootFolder, String fileName, Socket socket) {
        DataInputStream bis=null;
        BufferedOutputStream bos=null;
        String out =rootFolder+ FileUtils.parseExtToTxt(fileName);
        try {
            bis = new DataInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(out));
            long length = bis.readLong();
            System.out.println(fileName);
            System.out.println("--->len"+length);
            byte[] b = new byte[1024*1024];
            int l = 0;//一次读的长度
            int s = 0;//读了多少
            while ((s+=(l=bis.read(b)))!=length){
                bos.write(b,0,l);
                bos.flush();
            }
            System.out.println(length);
            System.out.println(s);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bos!=null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return out;
        }
    }
}
