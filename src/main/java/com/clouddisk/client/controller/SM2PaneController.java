package com.clouddisk.client.controller;

import com.cryptotool.cipher.MyCipher;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

@FXMLController
public class SM2PaneController {
    private static final int RADIO_DEC=1;
    private static final int RADIO_ENC=2;
    private int radio=2;
    @Autowired
    private MyCipher sm2Cipher;
    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private HBox radioHBox;

    @FXML
    private Button execute;

    @FXML
    void execute(ActionEvent event) {
        if (radio==RADIO_ENC){
            encrypt();
        }else if (radio==RADIO_DEC){
            decrypt();
        }
    }

    @FXML
    void initialize() {
        inputTextArea.setOnMouseClicked(e->{
            outputTextArea.setText("");
        });
        //单选框
        ToggleGroup group = new ToggleGroup();

        RadioButton encrypt= new RadioButton("加密");
        encrypt.setSelected(true);
        encrypt.setCursor(Cursor.HAND);
        encrypt.setToggleGroup(group);
        encrypt.setStyle("-fx-background-color: #51F43F; -fx-padding: 10,10,10,10; -fx-background-radius: 25;-fx-border-radius: 25");

        RadioButton decrypt= new RadioButton("解密");
        decrypt.setToggleGroup(group);
        decrypt.setCursor(Cursor.HAND);
        decrypt.setStyle("-fx-background-color: #51F43F; -fx-padding: 10,10,10,10; -fx-background-radius: 25;-fx-border-radius: 25");

        radioHBox.getChildren().add(encrypt);
        radioHBox.getChildren().add(decrypt);

        encrypt.setOnAction((e)->{
            radio=RADIO_ENC;
            inputTextArea.clear();
            inputTextArea.setPromptText("输入待加密的文本");
            outputTextArea.clear();
            outputTextArea.setPromptText("这里显示密文");
        });
        decrypt.setOnAction((e)->{
            radio=RADIO_DEC;
            inputTextArea.clear();
            inputTextArea.setPromptText("输入待解密的文本");
            outputTextArea.clear();
            outputTextArea.setPromptText("这里显示明文");
        });
    }
    private void encrypt(){
        String data = inputTextArea.getText();
        byte[] encrypt = sm2Cipher.encrypt(data.getBytes());
        String encData = Base64.getEncoder().encodeToString(encrypt);
        outputTextArea.setText(encData);
    }
    private void decrypt(){
        String data = inputTextArea.getText();
        byte[] encrypt = sm2Cipher.decrypt(Base64.getDecoder().decode(data));
        if (encrypt.length==0){
            outputTextArea.setText("密文格式不正确！");
            return;
        }
        String deData = new String(encrypt);
        outputTextArea.setText(deData);
    }
}
