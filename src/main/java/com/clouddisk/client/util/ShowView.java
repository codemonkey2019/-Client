package com.clouddisk.client.util;

import com.clouddisk.client.config.ApplicationContextProvider;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.GUIState;
import javafx.scene.Scene;

public class ShowView {
    public static void showView(Class<? extends AbstractFxmlView> view){
        AbstractFxmlView abstractFxmlView = ApplicationContextProvider.getBean(view);
        GUIState.setScene(new Scene(abstractFxmlView.getView()));
        GUIState.getStage().setScene(GUIState.getScene());
        GUIState.getStage().show();
    }
}
