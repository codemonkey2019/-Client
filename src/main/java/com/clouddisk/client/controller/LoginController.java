package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.HttpClientResult;
import com.clouddisk.client.util.HttpClientUtils;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

@FXMLController
public class LoginController {
    @FXML
    private TextField password;

    @FXML
    private Button loginSubmit;

    @FXML
    private Label welcome;

    @FXML
    private Button regist;

    @FXML
    private TextField username;

    @FXML
    void login(ActionEvent event) {
        System.out.println("login");
        Map<String,String> map = new HashMap(){
            {
                put("userName",username.getText());
                put("password",password.getText());
            }
        };
        HttpClientResult result=null;
        try {
           result= HttpClientUtils.doPost("http://127.0.0.1:8080/login",map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = result.getContent();
        System.out.println(JSON.parse(json));
    }

    @FXML
    void addAccount(ActionEvent event) {
        System.out.println("add");
    }
    @FXML
    void initialize() {
        System.out.println("ini");
    }
}
