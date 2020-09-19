package de.ebuchner.vocab.fx.platform;

import de.ebuchner.vocab.ApplicationInitializer;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class FxInitializer extends Application implements ApplicationInitializer {
    public void startVocab(String[] args) {
        launch(args);
    }

    @Override
    // todo primary stage not used???
    public void start(Stage stage) {
        System.setProperty(UIPlatformFactory.PLATFORM_PROPERTY, UIPlatformFactory.PLATFORM_PROPERTY_FX);
        FxUIPlatform.instance().getNuiDirector().startUp();
    }


}
