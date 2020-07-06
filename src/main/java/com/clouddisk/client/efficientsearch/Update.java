package com.clouddisk.client.efficientsearch;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.crypto.CryptoManager;
import com.cryptotool.digests.MyDigest;
import com.cryptotool.util.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Update {
    @Autowired
    private UserState userState;

    @Autowired
    private MyDigest sm3Digist;
    @Autowired
    private CryptoManager cryptoManager;


    public List<KFNode> update(String encFile, Map<String, String> keywords) {
        //拿到key
        byte[] key = cryptoManager.getSmServerKey().getForwardSearchKey();
        String ks = Base64.getEncoder().encodeToString(key);

        Set<String> keywordSet = keywords.keySet();
        List<KFNode> out = new ArrayList<>();
        //执行计算，拿到List<KFNode>
        keywordSet.forEach(wi -> {
            String e = null;
            String u = null;
            Set<String> Cind = new HashSet<>();

            String newState = keywords.get(wi);
            String tw = getPseo(ks, wi);
            String h1_tw_st = sm3Digist.getDigest(tw + newState);
            StateAndInd stateAndInd = new StateAndInd();
            if (userState.getKeywordState(wi) == null) {
                stateAndInd.setInd(encFile);
                e = stateAndIndOplushash(h1_tw_st, stateAndInd);
            } else {
                stateAndInd.setLastState(keywords.get(wi));
                stateAndInd.setInd(encFile);
                e = stateAndIndOplushash(h1_tw_st, stateAndInd);
            }
            u = sm3Digist.getDigest(tw + newState);

            keywordSet.forEach(wj -> {
                String dj = getPseo(ks, wi + wj);
                String cj = sm3Digist.getDigest(newState + dj);
                Cind.add(cj);
            });

            KFNode kfNode = new KFNode(u, e, Cind);
            out.add(kfNode);
        });
        return out;
    }

    private String stateAndIndOplushash(String hashState, StateAndInd stateAndInd) {
        String json = JSON.toJSONString(stateAndInd);
        byte[] byteJson = json.getBytes();
        byte b = hashState.getBytes()[0];
        byte[] outByte = new byte[byteJson.length];
        for (int i = 0; i < byteJson.length; i++) {
            byte a = (byte) (byteJson[i] ^ b);
            outByte[i]=a;
        }
        return HexUtils.binaryToHexString(outByte);
    }

    private String getPseo(String key, String data) {
        return sm3Digist.getDigest(key + data);
    }

}
