package com.clouddisk.client.controller;

import com.alibaba.fastjson.JSON;
import com.clouddisk.client.communication.MessageBody;
import com.clouddisk.client.communication.request.DownLoadRequest;
import com.clouddisk.client.communication.request.SearchRequest;
import com.clouddisk.client.communication.response.SearchAnswer;
import com.clouddisk.client.efficientsearch.FileSearchResult;
import com.clouddisk.client.efficientsearch.Search;
import com.clouddisk.client.util.FileUtils;
import com.clouddisk.client.util.InformationCast;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.cryptotool.cipher.MyCipher;
import com.cryptotool.util.HexUtils;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.Socket;
import java.util.*;

@FXMLController
@Slf4j
public class SearchPageController {

    @FXML
    private Label prosse;
    @Autowired
    private Search search;
    @Autowired
    private MyCipher sm4Cipher;
    @Autowired
    private MySocket mySocket;
    @FXML
    private TableColumn<FileSearchResult, String> selectCol;
    @FXML
    private TableColumn<FileSearchResult, String> filesCol;
    @FXML
    private TableView<FileSearchResult> fileTableView;
    @FXML
    private Button douwnloadButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button backToSearch;
    @FXML
    private Button searchAllButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField keywordField;

    @FXML
    private Pane keywordsPane;
    @FXML
    private FlowPane keywordPane;
    @FXML
    private Pane filesPane;
    @FXML
    private Button addButton;
    private List<String> keywords = new LinkedList<>();
    private Socket socket;
    private List<FileSearchResult> decFiles = new ArrayList<>();
    private List<String> encFiles = new ArrayList<>();
    private List<String> selectedFiles = new ArrayList<>();
    private Map<String,String> decFileToEncFile = new HashMap<>();
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    @FXML
    void backToSearch(ActionEvent event) {
        filesPane.setVisible(false);
        keywordsPane.setVisible(true);
        prosse.setText("");
    }
    @FXML
    void download(ActionEvent event) {
        log.info("待下载的文件："+selectedFiles.toString());
        //获取存入文件的文件夹
        String rootFolder = null;
        File rootFolderFile = directoryChooser.showDialog(GUIState.getStage());
        rootFolder= rootFolderFile.getAbsolutePath()+"/";
        //构造请求
        List<String> lf = new ArrayList<>();
        selectedFiles.forEach(f->{
            lf.add(decFileToEncFile.get(f));
        });
        MessageBody messageBody = new MessageBody("/download"
                ,JSON.toJSONString(new DownLoadRequest(lf)));
        //发送请求
        SocketConnect.sendMessageBodyToServer(socket,messageBody);
        //开始下载： 显示进度
        prosse.setText("正在下载");
        List<String> strings = downloadToFolder(socket, lf, rootFolder);
        decryptFiles(strings);
        prosse.setText("下载完成");
    }

    private void decryptFiles(List<String> strings) {
       strings.stream().forEach(a->{
           String s = FileUtils.parseTxtToExt(a);
           File in = new File(a);
           sm4Cipher.decryptFile(a,s);
           if (in.exists()){
               in.delete();
           }
       });
    }

    private List<String> downloadToFolder(Socket socket,List<String> files, String rootFolder) {
        List<String> decFileNames = decryptFileNames(files);
        List<String> out = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String fileName = decFileNames.get(i);
            String a = SocketConnect.douwnloadFile(rootFolder,fileName,socket);
           out.add(a);
        }
        return out;
    }



    @FXML
    void clear(ActionEvent event) {
        keywordPane.getChildren().clear();
        keywords.clear();
    }
    @FXML
    void searchAll(ActionEvent event) {
        encFiles.clear();
        decFiles.clear();
        decFileToEncFile.clear();
        MessageBody messageBody = new MessageBody("/searchAll",null);
        SocketConnect.sendMessageBodyToServer(socket,messageBody);
        MessageBody messageBodyFromServer = SocketConnect.getMessageBodyFromServer(socket);
        encFiles =  JSON.parseObject(messageBodyFromServer.getMessageBodyJSON(),SearchAnswer.class).getFileNames();
        decryptfiles();
        bindValues();
        keywordsPane.setVisible(false);
        filesPane.setVisible(true);
    }
    @FXML
    void search(ActionEvent event) {

        encFiles.clear();
        decFiles.clear();
        decFileToEncFile.clear();
        if (keywords.size()==0){
            return;
        }
        //生成搜索信息
        SearchRequest request = search.genToken(keywords);
        //本地判断
        if(request==null){
            alert();
        }
        //发送搜索信息
        MessageBody messageBody = new MessageBody("/search", JSON.toJSONString(request));
        SocketConnect.sendMessageBodyToServer(socket,messageBody);
        //接收搜索信息
        MessageBody answerBody = SocketConnect.getMessageBodyFromServer(socket);
        SearchAnswer answer = InformationCast.messageBodyToReqponseBody(answerBody,SearchAnswer.class);
        System.out.println(answer);
        encFiles = answer.getFileNames();
        //解密文件集
       decryptfiles();
        bindValues();
        //显示结果
        keywordsPane.setVisible(false);
        filesPane.setVisible(true);
    }

    private void bindValues() {
        //绑定数据
        ObservableList<FileSearchResult> lists = FXCollections.observableList(decFiles);
        filesCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        selectCol.setCellFactory((col) -> {
            TableCell<FileSearchResult, String> cell = new TableCell<FileSearchResult, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        CheckBox checkBox = new CheckBox();
                        this.setGraphic(checkBox);
                        checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
                            if (newVal) {
                                FileSearchResult fileSearchResult = this.getTableView().getItems().get(this.getIndex());
                                selectedFiles.add(fileSearchResult.getFileName());
                            }else {
                                FileSearchResult fileSearchResult = this.getTableView().getItems().get(this.getIndex());
                                selectedFiles.remove(fileSearchResult.getFileName());
                            }
                        });
                    }
                }
            };
            return cell;
        });
        fileTableView.setItems(lists);
    }


    private List<String> decryptFileNames(List<String> lists){
        List<String> out = new ArrayList<>();
        lists.forEach(e->{
            String encFileName=e.split("\\.")[0];
            byte[] decrypt = sm4Cipher.decrypt(HexUtils.hexStringToBinary(encFileName));
            String name = new String(decrypt);
            out.add(name);
        });
        return out;
    }

    private void decryptfiles() {
        encFiles.forEach(e->{
            String encFileName=e.split("\\.")[0];
            byte[] decrypt = sm4Cipher.decrypt(HexUtils.hexStringToBinary(encFileName));
            String name = new String(decrypt);
            decFiles.add(new FileSearchResult(name));
            decFileToEncFile.put(name,e);
        });
    }

    @FXML
    void add(ActionEvent event) {
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
        this.socket=mySocket.getSocket();
    }
    private class KeyWordLabel extends Label {
        public KeyWordLabel(String tittle){
            super(tittle);
            setStyle("-fx-border-radius: 25;-fx-background-radius: 25;-fx-alignment: center;-fx-pref-width: 135;-fx-pref-height: 36; -fx-background-color: #E18FC7;-fx-border-color: darkblue;-fx-border-insets: 0,0,0,0; -fx-hgap: 5;-fx-vgap: 10");
        }
    }
    private void alert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("搜索结果");
        alert.setHeaderText("本地匹配结果");
        alert.setContentText("没有相关文件");
        alert.show();
    }
}
