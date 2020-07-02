package com.clouddisk.client.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest implements Serializable {
    private String tw;
    private String stc;
    private Set<String> D;
}
