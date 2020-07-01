package com.clouddisk.client;

import com.clouddisk.client.config.MyConfiguration;
import com.clouddisk.client.config.MySplashScreen;
import com.clouddisk.client.efficientsearch.UserStateCacheManafer;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.clouddisk.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.net.Socket;
@Import(MyConfiguration.class)
@SpringBootApplication
public class ClientApplication extends AbstractJavaFxApplicationSupport implements ApplicationRunner {
    @Autowired
    private UserStateCacheManafer userStateCacheManafer;
    @Autowired
    private MySocket mySocket;
    public static void main(String[] args) {
        launch(ClientApplication.class, LoginView.class,new MySplashScreen(),args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file = new File("C:/MyCloudDisk/stateCache/");
        File file1 = new File("C:/MyCloudDisk/SMServerKey/");
        if (!file.exists()) {
            file.mkdir();
        }
        if (!file1.exists()) {
            file1.mkdir();
        }
        userStateCacheManafer.loadCache();
        Socket socket= SocketConnect.connectToServer();
        mySocket.setSocket(socket);
    }
}
