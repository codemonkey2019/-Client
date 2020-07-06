package com.clouddisk.client.efficientsearch;

import com.clouddisk.client.communication.request.SearchRequest;
import com.clouddisk.client.crypto.CryptoManager;
import com.clouddisk.client.crypto.SMServerKey;
import com.cryptotool.digests.MyDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Search {
    @Autowired
    private UserState userState;
    @Autowired
    private MyDigest sm3Digist;

    @Autowired
    private CryptoManager cryptoManager;


    public SearchRequest genToken(List<String> keywords) {
        //本地判断，如果不包含所有关键词，则直接返回null
        if (!userState.getState().keySet().containsAll(keywords)){
            return null;
        }
        byte[] key =  cryptoManager.getSmServerKey().getForwardSearchKey();
        String ks = Base64.getEncoder().encodeToString(key);
        String w1 = keywords.get(0);
        String stc = userState.getKeywordState(w1);
        String tw = getPseo(ks,w1);
        Set<String> D = new HashSet<>();
        String dj=null;
        for (int i = 0; i < keywords.size(); i++) {
            dj=getPseo(ks,w1+keywords.get(i));
            D.add(dj);
        }
        SearchRequest searchRequest = new SearchRequest(tw,stc,D);
        return searchRequest;
    }
    private String getPseo(String key, String data) {
        return sm3Digist.getDigest(key + data);
    }

}
