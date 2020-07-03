package com.clouddisk.client.communication.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAnswer implements Serializable {
    private boolean success;
}
