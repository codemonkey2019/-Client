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

import java.io.IOException;
import java.net.Socket;

/**
 * 登录界面的控制类
 */
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
            login();//执行登录
        }else {
            warnLable.setText("请输入完整信息");
        }
    }

    @FXML
    void addAccount(ActionEvent event) {
        ClientApplication.showView(RegistView.class);//显示注册界面
    }

    /**
     * 在页面显示之前做一些初始化操作：设置socket、一些事件等
     */
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

    /**
     * 登录逻辑
     */
    private void login(){
        LoginRequest loginRequest = new LoginRequest(username.getText(),password.getText());
        MessageBody messageBody = new MessageBody("/login", JSON.toJSONString(loginRequest));
        MessageBody request = SocketConnect.request(socket, messageBody);
        LoginAnswer loginAnswer = InformationCast.messageBodyToReqponseBody(request, LoginAnswer.class);
        if (loginAnswer.getSuccess()){
            ShowView.showView(MainPageView.class);
            userStateCacheManager.loadCache(loginRequest.getUserName());
            UserState.userName = loginRequest.getUserName();
            initCrypto(seed.getText().getBytes());
        }else {
            warnLable.setText("用户名或密码错误，请重新输入！");
        }
    }
    private void initCrypto(byte[] seed){
        SMServerKey smServerKey = KeyUtils.genSMServerKey(seed);
        cryptoManager.init(smServerKey);
    }

}
