package com.clouddisk.client.controller;

import com.cryptotool.digests.MyDigest;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Base64;

@FXMLController
public class SM3PaneController {
    @Autowired
    private MyDigest sm3Digist;
    private FileChooser fileChooser =new FileChooser();
    private File file;
    @FXML
    private Button inputButton;
    @FXML
    private Label fileName;

    @FXML
    private TextArea outputArea;

    @FXML
    private Button execute;

    @FXML
    void execute(ActionEvent event) {
        byte[] data = sm3Digist.getDigestOfFile(file.getAbsolutePath());
        outputArea.setText(Base64.getEncoder().encodeToString(data));
    }

    @FXML
    void showInputFileChooser(ActionEvent event) {
        outputArea.clear();
        fileChooser.setTitle("选着要哈希的文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Types", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Word", "*.doc"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        file = fileChooser.showOpenDialog(GUIState.getStage());
        if (file!=null) {
            fileName.setText(file.getName());
        }
    }

    @FXML
    void initialize() {
    }
}
