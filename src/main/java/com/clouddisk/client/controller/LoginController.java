package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.ClientApplication;
import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.communication.request.LoginRequest;
import com.clouddisk.client.communication.response.LoginAnswer;
import com.clouddisk.client.util.InformationCast;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.ShowView;
import com.clouddisk.client.util.SocketConnect;
import com.clouddisk.client.view.MainPageView;
import com.clouddisk.client.view.RegistView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Socket;

@FXMLController
@Slf4j
public class LoginController{
    @FXML
    private Label warnLable;
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
    @Autowired
    private MySocket mySocket;
    private Socket socket;
    @FXML
    void login(ActionEvent event) {
        if (!("".equals(username.getText())||("".equals(password.getText())))){
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
        }else {
            warnLable.setText("用户名或密码错误，请重新输入！");
        }
    }
}
