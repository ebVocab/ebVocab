package de.ebuchner.vocab.fx.practice;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.NuiWindowWithResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.practice.RepetitionLoadActivityBehaviour;
import de.ebuchner.vocab.model.practice.RepetitionLoadActivityController;
import de.ebuchner.vocab.model.practice.RepetitionLoadModel;
import de.ebuchner.vocab.nui.common.RepetitionItem;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RepetitionLoadWindow extends FxBaseWindow implements NuiWindowWithResult {

    private static Logger logger = Logger.getLogger(RepetitionLoadWindow.class.getName());
    private final RepetitionLoadActivityBehaviour myBehaviour = new MyBehaviour();
    private final RepetitionLoadModel model = new RepetitionLoadModel();
    private final RepetitionLoadActivityController controller =
            new RepetitionLoadActivityController(model, myBehaviour);
    private Map<String, Object> result = new HashMap<>();

    @FXML
    private Button btnDeleteSelected;

    @FXML
    private Button btnOk;

    @FXML
    private ListView<RepetitionItem> lvRepetitions;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.repetition.load.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "repetitionLoad";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
    }

    @Override
    public WindowType windowType() {
        return WindowType.REPETITION_LOAD_WINDOW;
    }

    @Override
    protected boolean isVocabWindowModal() {
        return true;
    }

    @Override
    protected void onBeforeShow() {
        RepetitionFileCellFactory.customize(lvRepetitions);

        for (File repetitionFile :
                Config.instance().listAutoSavedFiles(
                        ConfigConstants.REPETITION_FILES_SUFFIX,
                        ConfigConstants.FILE_REF_EXTENSION
                ))
            try {
                lvRepetitions.getItems().add(new RepetitionItem(repetitionFile));
            } catch (Exception e) {
                boolean deleted = repetitionFile.delete();
                logger.info(String.format("Deleted invalid auto saved file %s: %s",
                        repetitionFile.getName(),
                        deleted ? "ok" : "failed"
                ));
            }

        lvRepetitions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        myBehaviour.onModelChanged(model);
    }

    public void onOk(ActionEvent actionEvent) {
        controller.onOk();
    }

    public void onDeleteSelected(ActionEvent actionEvent) {
        controller.onDeleteSelected();
    }

    public void onCancel(ActionEvent actionEvent) {
        controller.onCancel();
    }

    public void onSelectionChanged(Event event) {
        RepetitionItem item = lvRepetitions.getSelectionModel().getSelectedItem();
        if (item == null)
            controller.onRepetitionSelectionCleared();
        else
            controller.onRepetitionSelected(item.getRepetitionFile());
    }

    @Override
    public Map<String, Object> getResult() {
        return result;
    }

    private class MyBehaviour implements RepetitionLoadActivityBehaviour {
        @Override
        public void sendRepetitionToCallerAndExit(File repetitionFile) {
            result.put(RepetitionLoadModel.REPETITION_FILE, repetitionFile);
            attemptClosing();
        }

        @Override
        public void sendCancelToCallerAndExit() {
            result.clear();
            attemptClosing();
        }

        @Override
        public void onModelChanged(RepetitionLoadModel model) {
            btnOk.setDisable(!model.hasRepetitionFile());
            btnDeleteSelected.setDisable(!model.hasRepetitionFile());
        }

        @Override
        public void selectedRepetitionDeleted() {
            int index = lvRepetitions.getSelectionModel().getSelectedIndex();
            if (index < 0)
                return;
            lvRepetitions.getItems().remove(index);
        }
    }
}