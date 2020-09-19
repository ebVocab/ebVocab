package de.ebuchner.vocab.fx.common;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressDialog {

    private Stage stage;
    private ProgressIndicator progressCtrl = new ProgressIndicator();

    public ProgressDialog() {
        stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        progressCtrl.setProgress(-1F);

        Scene scene = new Scene(progressCtrl);
        stage.setScene(scene);
    }

    public void addTask(final Task<?> task) {
        progressCtrl.progressProperty().bind(task.progressProperty());
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
