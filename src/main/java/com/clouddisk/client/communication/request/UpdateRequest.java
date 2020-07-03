package com.clouddisk.client.communication.request;

import com.clouddisk.client.efficientsearch.KFNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest implements Serializable {
    private List<KFNode> kfNodes;
    private String fileName;
}
