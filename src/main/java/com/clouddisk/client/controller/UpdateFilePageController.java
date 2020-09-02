package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.communication.request.UpdateRequest;
import com.clouddisk.client.communication.response.UpdateAnswer;
import com.clouddisk.client.crypto.CryptoManager;
import com.clouddisk.client.efficientsearch.KFNode;
import com.clouddisk.client.efficientsearch.Update;
import com.clouddisk.client.efficientsearch.UserState;
import com.clouddisk.client.efficientsearch.UserStateCacheManager;
import com.clouddisk.client.util.InformationCast;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.digests.MyDigest;
import com.cryptotool.util.HexUtils;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.Socket;
import java.util.*;

@FXMLController
@Slf4j
public class UpdateFilePageController {
    @Autowired
    private MyDigest sm3Digist;
    @Autowired
    private CryptoManager cryptoManager;
    @Autowired
    private Update update;
    @Autowired
    private MySocket mySocket;
    @Autowired
    private UserState userState;
    @Autowired
    private UserStateCacheManager userStateCacheManager;

    @FXML
    private Label partPersen;
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

    private MyCipher sm4Cipher;

    private Socket socket;
    private Random random = new Random();
    private final String tempCachePath="C:/MyCloudDisk/TempEncFileCache/";
    private FileChooser fileChooser=new FileChooser();
    private File file;
    private List<String> keywords = new LinkedList<>();

    @FXML
    void submit(ActionEvent event) {
        if (keywords.size()==0){
            partPersen.setText("请添加关键词");
            return;
        }
        partPersen.setText("正在上传");
        //计算请求体
        UpdateRequest request = new UpdateRequest();
        //0.计算密文文件名
        String encryptHexFileName = encryptFileNameToHexString(this.file.getName());
        request.setFileName(encryptHexFileName);
            //1. 计算新增节点
        Map<String,String> keywordAndState = new HashMap<>();
            //先生成新的状态集合
        List<String> keywordSate= generateState(keywords.size());

        for (int i = 0; i < keywords.size(); i++) {
            keywordAndState.put(keywords.get(i),keywordSate.get(i));
        }
        //生成updateToken
        List<KFNode> kfNodes = this.update.update(encryptHexFileName, keywordAndState);
        request.setKfNodes(kfNodes);
        //创建请求对象
        MessageBody messageBody = new MessageBody();
        messageBody.setPath("/update");
        messageBody.setMessageBodyJSON(JSON.toJSONString(request));
        //3. 发送MessageBody到服务器
        SocketConnect.sendMessageBodyToServer(this.socket,messageBody);
        //加密文件
        File fileFolder = new File(tempCachePath);
        String outFile =tempCachePath+encryptHexFileName;
            sm4Cipher.encryptFile(file.getAbsolutePath(),outFile);
        //将密文文件传送给服务器
        boolean b = SocketConnect.sendFileToServer(socket,outFile);
        //接收响应信息
        MessageBody messageBodyFromServer = SocketConnect.getMessageBodyFromServer(socket);
        //转换响应信息
        UpdateAnswer answer = InformationCast.messageBodyToReqponseBody(messageBodyFromServer,UpdateAnswer.class);

        //若成功，则更新本地状态集合
        if (b&&answer.isSuccess()) {
            partPersen.setText("上传成功");
            keywordAndState.forEach((k,v)->{
                userState.addKeywordState(k,v);
            });
            userStateCacheManager.refreshCach(UserState.userName);
            //返回到初始状态
            clear(new ActionEvent());
            fileNameLabel.setText("");
            addKeyWordButton.setDisable(true);
            keywordField.setEditable(false);
            clearButton.setDisable(true);
            submitButton.setDisable(true);
            keywordField.setDisable(true);
        }
        File del = new File(outFile);
        if (del.exists()) {
            del.delete();
        }
        //弹窗显示响应信息
        alert(answer.isSuccess());
    }

    @FXML
    void fileChoose(ActionEvent event) {
        partPersen.setText("");
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
        keywordField.setDisable(false);
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
            log.info("添加关键词"+keywordField.getText());
            keyword=keywordField.getText();
            keywords.add(keyword);
            KeyWordLabel keyWordLabel = new KeyWordLabel(keyword);
            keywordPane.getChildren().add(keyWordLabel);
            keywordField.clear();
        }
    }
    @FXML
    void initialize() {
        this.sm4Cipher=cryptoManager.getSm4Cipher();
        this.socket=mySocket.getSocket();
        keywordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    addKeyWord(null);
                }
            }
        });
    }
    private class KeyWordLabel extends Label{
        public KeyWordLabel(String tittle){
            super(tittle);
            setStyle("-fx-border-radius: 25;-fx-background-radius: 25;-fx-alignment: center;-fx-pref-width: 135;-fx-pref-height: 36; -fx-background-color: #E18FC7;-fx-border-color: darkblue;-fx-border-insets: 0,0,0,0; -fx-hgap: 5;-fx-vgap: 10");
        }
    }

    /**
     * 将文件名加密然后转为16进制字符串,已经带上 .txt 扩展名
     * @param fileName
     * @return
     */
    private String encryptFileNameToHexString(String fileName){
        byte[] encryptBytes = sm4Cipher.encrypt(fileName.getBytes());
        return HexUtils.binaryToHexString(encryptBytes)+".txt";
    }
    private void alert(boolean success){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                         alert.setTitle("提交结果");
        if (success){
            alert.setHeaderText("提交成功");
            alert.setContentText("本次文件更新成功");
        }else {
            alert.setHeaderText("提交失败");
            alert.setContentText("本次文件更新失败，请重新提交");
        }
        alert.show();
    }
    //为每一个关键词生成新的状态
    private List<String> generateState(int len) {
        List<String> states = new ArrayList<>();
        String stateString=null;
        for (int i = 0; i < len; i++) {
            stateString= sm3Digist.getDigest(random.nextInt(100)+"");
            states.add(stateString);
        }
        return states;
    }
}
