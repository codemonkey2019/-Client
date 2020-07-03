package com.clouddisk.client.efficientsearch;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStateCacheManager {
    @Autowired
    private UserState userState;

    /**
     * 这个方法不应由用户调用
     */
    public void loadCache(){
        File cache = new File(UserState.STATE_CACHE_PATH);
        if (!cache.exists()){
            return;
        }
        BufferedReader br=null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(cache)));
            String state = br.readLine();
            Map<String,String>  cach = JSON.parseObject(state, ConcurrentHashMap.class);
            userState.setState(cach);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 每上传一个文件都更新缓存文件
     */
    public void refreshCach(){
        File cache = new File(UserState.STATE_CACHE_PATH);
        BufferedWriter bw=null;
        try {
            bw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache)));
            String cachdata = JSON.toJSONString(userState.getState());
            bw.write(cachdata);
            bw.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (bw!=null) {
                try {
                    bw.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
