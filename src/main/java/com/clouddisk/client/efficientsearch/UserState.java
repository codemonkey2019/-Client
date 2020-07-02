package com.clouddisk.client.efficientsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class UserState {
    public static final String STATE_CACHE_PATH="C:/MyCloudDisk/stateCache/stateCache.cache";
    @Setter
    @Getter
    private Map<String,String> state = new ConcurrentHashMap<>();

    public void addKeywordState(String keyword, String newState){
        state.put(keyword,newState);
    }
    public String getKeywordState(String keyword){
            return state.get(keyword);
    }

}
