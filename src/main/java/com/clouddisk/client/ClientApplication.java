package com.clouddisk.client;

import com.clouddisk.client.config.MyConfiguration;
import com.clouddisk.client.config.MySplashScreen;
import com.clouddisk.client.util.MySocket;
import com.clouddisk.client.util.SocketConnect;
import com.clouddisk.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.net.Socket;
@Import(MyConfiguration.class)
@SpringBootApplication
public class ClientApplication extends AbstractJavaFxApplicationSupport implements ApplicationRunner {
    @Value("${ip}")
    private String ip;
    @Autowired
    private MySocket mySocket;
    public static void main(String[] args) {
        launch(ClientApplication.class, LoginView.class,new MySplashScreen(),args);
    }

    /**
     * 在springboot启动之后检查文件夹、连接服务器
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file2 = new File("C:/MyCloudDisk/TempEncFileCache/");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        Socket socket= SocketConnect.connectToServer(ip);
        mySocket.setSocket(socket);
    }
}
