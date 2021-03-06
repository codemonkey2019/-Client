package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.ClientApplication;
import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.communication.request.RegistRequest;
import com.clouddisk.client.communication.response.RegistAnswer;
import com.clouddisk.client.util.InformationCast;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.clouddisk.client.view.LoginView;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.util.MyStringUtils;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.Socket;

/**
 * 注册页面控制器
 */
@FXMLController
@Slf4j
public class RegistController  {

    @FXML
    private Button registSubmit;

    @FXML
    private TextField password;

    @FXML
    private Button back;

    @FXML
    private Label warnLable;

    @FXML
    private Label welcome;

    @FXML
    private TextField username;

    @Autowired
    private MySocket mySocket;
    @Autowired
    private MyCipher serverSM2EncryptCipher;
    private Socket socket;

    /**
     * 注册按钮事件
     * @param event
     */
    @FXML
    void regist(ActionEvent event) {
        if (!("".equals(username.getText())||("".equals(password.getText())))){
            regist();//提交注册
        }else {
            warnLable.setText("请输入完整信息");
        }
    }

    @FXML
    void back(ActionEvent event) {
        ClientApplication.showView(LoginView.class);
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

    private void regist(){
        RegistRequest registRequest = new RegistRequest(username.getText(),password.getText());
        byte[] cipherText = serverSM2EncryptCipher.encrypt(JSON.toJSONBytes(registRequest));
        MessageBody messageBody = new MessageBody("/regist", MyStringUtils.encodeToBase64String(cipherText));
        MessageBody request = SocketConnect.request(socket, messageBody);
        RegistAnswer registAnswer = InformationCast.messageBodyToReqponseBody(request, RegistAnswer.class);
        if (!registAnswer.getSuccess()){
            warnLable.setText("注册失败，用户名已存在！");
        }else {
            warnLable.setText("注册成功，请返回登录！");
        }
    }
    @PostConstruct
    public void init(){
        this.socket=mySocket.getSocket();
    }


}
