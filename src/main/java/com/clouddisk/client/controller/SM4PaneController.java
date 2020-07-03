package com.clouddisk.client.controller;

import com.clouddisk.client.util.FileUtils;
import com.cryptotool.cipher.MyCipher;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@FXMLController
@Slf4j
public class SM4PaneController {
    private static final int RADIO_DEC=1;
    private static final int RADIO_ENC=2;
    private int radio=RADIO_ENC;
    private File inputFile;
    private String outputFolder;
    @Autowired
    private MyCipher sm4Cipher;
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser= new DirectoryChooser();
    @FXML
    private Button inputButton;

    @FXML
    private Button outputButton;

    @FXML
    private HBox radioHBox;

    @FXML
    private Button execute;
    @FXML
    private TextField outputFileName;

    @FXML
    private TextField inputFileName;
    @FXML
    private Label done;
    @FXML
    void execute(ActionEvent event) {
        if (radio==RADIO_ENC){
            encrypt();
        }else if (radio==RADIO_DEC){
            decrypt();
        }
        done.setText("完成");
    }

    @FXML
    void showInputFileChooser(ActionEvent event) {


        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Types", "*.*")
        );
        fileChooser.setTitle("选择你的待处理文件");
        inputFile = fileChooser.showOpenDialog(GUIState.getStage());
        if (inputFile!=null){
            inputFileName.setText(inputFile.getName());
        }
    }

    @FXML
    void showOutputFileChooser(ActionEvent event) {
        directoryChooser.setTitle("选择你的输出文件的位置");
        outputFolder = directoryChooser.showDialog(GUIState.getStage()).getAbsolutePath();
        if (outputFolder != null) {
            outputFileName.setText(outputFolder);
        }
    }

    @FXML
    void initialize() {
        //单选框
        ToggleGroup group = new ToggleGroup();

        RadioButton encrypt = new RadioButton("加密");
        encrypt.setSelected(true);
        encrypt.setToggleGroup(group);
        encrypt.setStyle("-fx-background-color: #51F43F; -fx-padding: 10,10,10,10; -fx-background-radius: 25;-fx-border-radius: 25");
        encrypt.setCursor(Cursor.HAND);

        RadioButton decrypt = new RadioButton("解密");
        decrypt.setToggleGroup(group);
        decrypt.setStyle("-fx-background-color: #51F43F; -fx-padding: 10,10,10,10; -fx-background-radius: 25;-fx-border-radius: 25");
        decrypt.setCursor(Cursor.HAND);

        radioHBox.getChildren().add(encrypt);
        radioHBox.getChildren().add(decrypt);
        encrypt.setOnAction((e)->{
            radio=RADIO_ENC;
            inputFileName.setText("");
            outputFileName.setText("");
            done.setText("");
        });
        decrypt.setOnAction((e)->{
            radio=RADIO_DEC;
            inputFileName.setText("");
            outputFileName.setText("");
            done.setText("");
        });
    }

    private void encrypt() {
        String[] ss = inputFile.getName().split("\\.");
        String a =ss[0];
        String ext = ss[1];
        sm4Cipher.encryptFile(inputFile.getAbsolutePath(),outputFolder+"/"+FileUtils.parseExtToTxt(inputFile.getName()));
    }

    private void decrypt() {
       sm4Cipher.decryptFile(inputFile.getAbsolutePath(),outputFolder+"/"+FileUtils.parseTxtToExt(inputFile.getName()));
    }
}
