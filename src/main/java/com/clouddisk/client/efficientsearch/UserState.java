package com.clouddisk.client.efficientsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserState {
    public static final String STATE_CACHE_PATH="C:/MyCloudDisk/stateCache/stateCache.cache";
    private Map<String,String> state = new ConcurrentHashMap<>();

    public void addState(String keyword,String newState){
        state.put(keyword,newState);
    }
    public String getState(String keyword){
        return state.get(keyword);
    }

}
