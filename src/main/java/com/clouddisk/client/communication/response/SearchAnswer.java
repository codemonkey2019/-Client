package com.clouddisk.client.communication.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchAnswer implements Serializable {
    private boolean success;
    private List<String> fileNames;
}
