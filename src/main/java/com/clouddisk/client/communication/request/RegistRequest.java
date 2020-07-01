package com.clouddisk.client.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistRequest implements Serializable {
    private String userName;
    private String password;
}
