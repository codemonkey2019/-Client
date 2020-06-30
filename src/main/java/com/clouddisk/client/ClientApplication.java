package com.clouddisk.client;

import com.clouddisk.client.config.MySplashScreen;
import com.clouddisk.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ClientApplication.class, LoginView.class,new MySplashScreen(),args);
    }
}
