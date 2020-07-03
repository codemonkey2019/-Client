package com.clouddisk.client.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBody implements Serializable {
    private String path;
    private String messageBodyJSON;
}
