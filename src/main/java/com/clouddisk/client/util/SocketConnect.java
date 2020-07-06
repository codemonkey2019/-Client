package com.clouddisk.client.util;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class SocketConnect {

    public static boolean sendFileToServer(Socket socket, String filePath){
        boolean success = false;
        File sendFile = new File(filePath);
        long length = sendFile.length();
        if (sendFile.exists()) {
            DataInputStream bis=null;
            DataOutputStream dos =null;
            try {
                bis = new DataInputStream(new FileInputStream(sendFile));
                dos= new DataOutputStream(socket.getOutputStream());
                //发送文件长度
                dos.writeLong(sendFile.length());
                dos.flush();
                byte[] b = new byte[1024];
                int len = 0;
                while ((len = bis.read(b))!=-1){
                    dos.write(b,0,len);
                    dos.flush();
                }
                success=true;
            } catch (IOException e) {
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
        String out =rootFolder+FileUtils.getRandomString()+ FileUtils.parseExtToTxt(fileName);
        try {
            bis = new DataInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(out));
            long length = bis.readLong();
            long a = length/1024;
            long c = length%1024;
            byte[] b = new byte[1024];
            byte[] bb = new byte[(int) c];
            int len = 0;//一次读的长度
            long sum = 0L;//读了多少
            for (long i = 0; i < a; i++) {
                bis.read(b);
                bos.write(b);
                bos.flush();
            }
            bis.read(bb);
            bos.write(bb);
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
