package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.ClientApplication;
import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.communication.request.LoginRequest;
import com.clouddisk.client.communication.response.LoginAnswer;
import com.clouddisk.client.crypto.CryptoManager;
import com.clouddisk.client.crypto.SMServerKey;
import com.clouddisk.client.efficientsearch.UserState;
import com.clouddisk.client.efficientsearch.UserStateCacheManager;
import com.clouddisk.client.util.*;
import com.clouddisk.client.view.MainPageView;
import com.clouddisk.client.view.RegistView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

@FXMLController
@Slf4j
public class LoginController {
    @FXML
    private Label warnLable;
    @FXML
    private TextField password;
    @FXML
    private PasswordField seed;
    @FXML
    private Button loginSubmit;

    @FXML
    private Label welcome;

    @FXML
    private Button regist;

    @FXML
    private TextField username;
    @Autowired
    private UserStateCacheManager userStateCacheManager;
    @Autowired
    private CryptoManager cryptoManager;

    @Autowired
    private MySocket mySocket;
    private Socket socket;
    @FXML
    void login(ActionEvent event) {
        if (!("".equals(username.getText())||("".equals(password.getText()))||("".equals(seed.getText())))){
            login();
        }else {
            warnLable.setText("请输入完整信息");
        }
    }

    @FXML
    void addAccount(ActionEvent event) {
        ClientApplication.showView(RegistView.class);
    }
    @FXML
    void initialize() {
        username.setOnMouseClicked(e -> {
            warnLable.setText("");
        });
        password.setOnMouseClicked(e -> {
            warnLable.setText("");
        });
        this.socket = mySocket.getSocket();

        GUIState.getStage().setOnCloseRequest(e->{
            try {
                log.info("正在关闭连接");
                MessageBody messageBody = new MessageBody("/close", null);
                MessageBody request = SocketConnect.request(socket, messageBody);
                mySocket.getSocket().close();
            } catch (IOException ioException) {
                log.info("链接关闭失败");
            }
        });
    }
    private void login(){
        LoginRequest loginRequest = new LoginRequest(username.getText(),password.getText());
        MessageBody messageBody = new MessageBody("/login", JSON.toJSONString(loginRequest));
        MessageBody request = SocketConnect.request(socket, messageBody);
        LoginAnswer loginAnswer = InformationCast.messageBodyToReqponseBody(request, LoginAnswer.class);
        if (loginAnswer.getSuccess()){
            ShowView.showView(MainPageView.class);
            userStateCacheManager.loadCache(loginRequest.getUserName());
            UserState.userName = loginRequest.getUserName();
            initCrypto(loginRequest.getUserName(),seed.getText().getBytes());
        }else {
            warnLable.setText("用户名或密码错误，请重新输入！");
        }
    }
    private void initCrypto(String username, byte[] seed){
        String basePath = "C:/MyCloudDisk/"+username+"/SMServerKey/";
        File f1 = new File(basePath+"ec.pkcs8.pri.der");
        File f2 = new File(basePath+"ec.x509.pub.der");
        File f3 = new File(basePath+"sm4.key");
        File f4 = new File(basePath+"forwardSearchKey.key");
        if (!f1.exists()||!f2.exists()||!f3.exists()||!f4.exists()){
            KeyUtils.genSMServerKeyByUserNameToFile(username,seed);
        }
        SMServerKey smServerKey = KeyUtils.getSMServerKeyByNameFromFile(username);
        cryptoManager.init(smServerKey);
    }

}
