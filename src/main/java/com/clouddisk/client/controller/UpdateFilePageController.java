package com.clouddisk.client.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@FXMLController
public class UpdateFilePageController {
    private FileChooser fileChooser=new FileChooser();
    private File file;
    private List<String> keywords = new LinkedList<>();
    @FXML
    private Pane keyWordAddPane;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Button addKeyWordButton;

    @FXML
    private FlowPane keywordPane;

    @FXML
    private TextField keywordField;
    @FXML
    private Button submitButton;
    @FXML
    private Button fileChooseButton;
    @FXML
    private Button clearButton;

    @FXML
    void submit(ActionEvent event) {

    }
    @FXML
    void fileChoose(ActionEvent event) {
        file = fileChooser.showOpenDialog(GUIState.getStage());
        if (file!=null){
            fileNameLabel.setText(file.getName());
            addKeyWordButton.setDisable(false);
            keywordField.setEditable(true);
            clearButton.setDisable(false);
            submitButton.setDisable(false);
        }else {
            fileNameLabel.setText("重新选取文件");
        }
    }
    @FXML
    void clear(ActionEvent event) {
        keywordPane.getChildren().clear();
        keywords.clear();
    }
    @FXML
    void reset(ActionEvent event) {
        keywordField.clear();
    }
    @FXML
    void addKeyWord(ActionEvent event) {
        String keyword=null;
        if (!keywordField.getText().equals("")){
            keyword=keywordField.getText();
            keywords.add(keyword);
            KeyWordLabel keyWordLabel = new KeyWordLabel(keyword);
            keywordPane.getChildren().add(keyWordLabel);
            keywordField.clear();
        }
    }
    @FXML
    void initialize() {
    }
    private class KeyWordLabel extends Label{
        public KeyWordLabel(String tittle){
            super(tittle);
            setStyle("-fx-border-radius: 25;-fx-background-radius: 25;-fx-alignment: center;-fx-pref-width: 135;-fx-pref-height: 36; -fx-background-color: #E18FC7;-fx-border-color: darkblue;-fx-border-insets: 0,0,0,0; -fx-hgap: 5;-fx-vgap: 10");
        }
    }
}
