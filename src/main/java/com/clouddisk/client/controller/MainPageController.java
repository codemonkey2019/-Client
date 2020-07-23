package com.clouddisk.client.controller;

import com.clouddisk.client.view.CryptoPageView;
import com.clouddisk.client.view.SearchPageView;
import com.clouddisk.client.view.UpdateFilePageView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 主功能页面，主要控制显示不同的功能窗口
 */
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
    private CryptoPageView cryptoPageView;
    @Autowired
    private UpdateFilePageView updateFilePageView;
    @Autowired
    private SearchPageView searchPageView;


    @FXML
    void showCryptoPage(ActionEvent event) {
        dynamicPane.getChildren().clear();
        dynamicPane.getChildren().add(cryptoPageView.getView());
    }

    @FXML
    void showFiles(ActionEvent event) {
        dynamicPane.getChildren().clear();
        dynamicPane.getChildren().add(searchPageView.getView());
    }

    @FXML
    void showUploadFilePage(ActionEvent event) {
        dynamicPane.getChildren().clear();
        dynamicPane.getChildren().add(updateFilePageView.getView());
    }
}
