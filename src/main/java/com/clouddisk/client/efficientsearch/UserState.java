package com.clouddisk.client.efficientsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class UserState {
    public static String userName = null;
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
