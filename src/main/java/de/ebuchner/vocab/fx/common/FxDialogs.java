package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FxDialogs {

    private final Stage stage;
    private final FlowPane buttonsPane;
    private final Label message;
    private final BorderPane dialogPane;

    private I18NContext i18n = I18NLocator.locate();

    private Action selectedAction = null;

    private FxDialogs() {
        this.stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        dialogPane = new BorderPane();
        dialogPane.setPadding(new Insets(3, 3, 3, 3));

        this.buttonsPane = new FlowPane();
        buttonsPane.setPadding(new Insets(3, 3, 3, 3));
        buttonsPane.setHgap(3);
        buttonsPane.setVgap(3);
        buttonsPane.setAlignment(Pos.BASELINE_RIGHT);

        dialogPane.setBottom(buttonsPane);

        message = new Label();
        dialogPane.setTop(message);

        stage.setScene(new Scene(dialogPane));
    }

    public static FxDialogs create() {
        return new FxDialogs();
    }

    public FxDialogs message(String text) {
        message.setText(text);
        return this;
    }

    public FxDialogs title(String title) {
        stage.setTitle(title);
        return this;
    }

    public FxDialogs actions(ActionType... types) {
        for (ActionType type : types) {
            if (type == ActionType.OK)
                buttonsPane.getChildren().add(createButton(type, "nui.ok"));
            else if (type == ActionType.YES)
                buttonsPane.getChildren().add(createButton(type, "nui.yes"));
            else if (type == ActionType.NO)
                buttonsPane.getChildren().add(createButton(type, "nui.no"));
            else if (type == ActionType.CANCEL)
                buttonsPane.getChildren().add(createButton(type, "nui.cancel"));
        }
        return this;
    }

    private Button createButton(ActionType type, String resKey) {
        Button button = new Button();
        button.setText(i18n.getString(resKey));
        button.setOnAction(new ActionHandler(type));

        return button;
    }

    public void showInformation() {
        if (buttonsPane.getChildren().isEmpty())
            buttonsPane.getChildren().add(createButton(ActionType.OK, "nui.ok"));

        stage.showAndWait();
    }

    public Action showConfirm() {
        if (buttonsPane.getChildren().isEmpty()) {
            buttonsPane.getChildren().add(createButton(ActionType.OK, "nui.ok"));
            buttonsPane.getChildren().add(createButton(ActionType.CANCEL, "nui.cancel"));
        }

        stage.showAndWait();
        return selectedAction;
    }

    public Action showTextInput() {
        TextField textField = new TextField();
        textField.setPrefColumnCount(10);
        dialogPane.setCenter(textField);

        showInformation();
        if (selectedAction.getActionType() == ActionType.OK)
            selectedAction.setTextInput(textField.getText());
        return selectedAction;
    }

    public static enum ActionType {
        YES, CANCEL, NO, OK
    }

    public static class Action {
        private final ActionType actionType;
        private String textInput;

        public Action(ActionType actionType) {
            this.actionType = actionType;
        }

        public ActionType getActionType() {
            return actionType;
        }

        public String getTextInput() {
            return textInput;
        }

        public void setTextInput(String textInput) {
            this.textInput = textInput;
        }
    }

    class ActionHandler implements EventHandler<ActionEvent> {
        private final Action action;

        public ActionHandler(ActionType actionType) {
            this.action = new Action(actionType);
        }

        @Override
        public void handle(ActionEvent event) {
            selectedAction = action;
            stage.close();
        }
    }
}
