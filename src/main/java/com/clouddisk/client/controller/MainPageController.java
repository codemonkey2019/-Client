package com.clouddisk.client.controller;

import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.clouddisk.client.view.CryptoPageView;
import com.clouddisk.client.view.UpdateFilePageView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Socket;

@FXMLController
@Slf4j
public class MainPageController {
    @FXML
    private Button showFilesButton;

    @FXML
    private Button cryptoButton;

    @FXML
    private Button uploadFileButton;

    @FXML
    private Pane dynamicPane;

    @Autowired
    private MySocket mySocket;
    @Autowired
    private CryptoPageView cryptoPageView;
    @Autowired
    private UpdateFilePageView updateFilePageView;
    private Socket socket;

    @FXML
    void showCryptoPage(ActionEvent event) {
        dynamicPane.getChildren().clear();
        dynamicPane.getChildren().add(cryptoPageView.getView());
    }

    @FXML
    void showFiles(ActionEvent event) {

    }

    @FXML
    void showUploadFilePage(ActionEvent event) {
        dynamicPane.getChildren().clear();
        dynamicPane.getChildren().add(updateFilePageView.getView());
    }
    @FXML
    void initialize() {
        this.socket=mySocket.getSocket();
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
}
