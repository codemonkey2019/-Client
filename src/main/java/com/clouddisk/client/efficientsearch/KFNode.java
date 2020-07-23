package com.clouddisk.client.efficientsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 更新Token的类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KFNode {
    private String u;
    private String e;
    private Set<String> Cind;
}
