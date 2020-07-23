package com.clouddisk.client.controller;

import com.clouddisk.client.view.SM2PaneView;
import com.clouddisk.client.view.SM3PaneView;
import com.clouddisk.client.view.SM4PaneView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 加解密工具页面的控制器
 */
@FXMLController
@Slf4j
public class CryptoPageController {

    @FXML
    private Pane cryptoDynamicPane;

    @FXML
    private Button SM3;

    @FXML
    private Button SM2;

    @FXML
    private Button SM4;

    @Autowired
    private SM2PaneView sm2PaneView;
    @Autowired
    private SM3PaneView sm3PaneView;
    @Autowired
    private SM4PaneView sm4PaneView;

    /**
     * 跳转到sm2加解密页面
     * @param event
     */
    @FXML
    void sm2Crypto(ActionEvent event) {
        cryptoDynamicPane.getChildren().clear();
        cryptoDynamicPane.getChildren().add(sm2PaneView.getView());
    }

    @FXML
    void sm3Crypto(ActionEvent event) {
        cryptoDynamicPane.getChildren().clear();
        cryptoDynamicPane.getChildren().add(sm3PaneView.getView());
    }

    @FXML
    void sm4Crypto(ActionEvent event) {
        cryptoDynamicPane.getChildren().clear();
        cryptoDynamicPane.getChildren().add(sm4PaneView.getView());
    }
}
