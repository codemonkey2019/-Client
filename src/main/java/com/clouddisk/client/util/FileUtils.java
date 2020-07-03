package com.clouddisk.client.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils {

    public static String parseTxtToExt(String fileName){
        String ss = fileName.split("\\.")[0];
        String[] sss = ss.split("_");
        String name = sss[0];
        String ext = sss[1];
        return name+"."+ext;
    }

    public static String parseExtToTxt(String fileName){
        String[] ss = fileName.split("\\.");
        String a =ss[0];
        String ext = ss[1];
        return a+"_"+ext+".txt";
    }

    public static void writeFile(String filePath, byte[] data) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            raf.write(data);
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    public static byte[] readFile(String filePath) throws IOException {
        RandomAccessFile raf = null;
        byte[] data;
        try {
            raf = new RandomAccessFile(filePath, "r");
            data = new byte[(int) raf.length()];
            raf.read(data);
            return data;
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }
}
