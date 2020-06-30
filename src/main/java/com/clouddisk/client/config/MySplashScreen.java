package com.clouddisk.client.config;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Parent;

public class MySplashScreen extends SplashScreen {
    public MySplashScreen() {
        super();
    }

    @Override
    public Parent getParent() {
        return super.getParent();
    }

    @Override
    public boolean visible() {
        return false;
    }

//    @Override
//    public String getImagePath() {
//        return "icon.jpg";
//    }
}
