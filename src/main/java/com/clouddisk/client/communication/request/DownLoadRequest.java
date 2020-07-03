package com.clouddisk.client.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownLoadRequest implements Serializable {
    private List<String> selectedFiles;
}
