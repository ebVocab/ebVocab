package de.ebuchner.vocab.fx.transliteration;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.nui.focus.FocusAware;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import de.ebuchner.vocab.model.transliteration.TransliterationModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class TransliterationWindow extends FxBaseWindow implements FocusAware {

    @FXML
    private GridPane gridPane;
    @FXML
    private ToggleButton tbOnTop;

    private NuiTextFieldWithFocus textFieldWithFocus;
    private TransliterationWindowController controller = new TransliterationWindowController();


    @Override
    protected String vocabWindowTitleKey() {
        return "nui.transliteration.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "transliteration";
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
        return WindowType.TRANSLITERATION_WINDOW;
    }

    @Override
    public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {
        this.textFieldWithFocus = textFieldWithFocus;
    }

    @Override
    public void onFocusLost() {
        this.textFieldWithFocus = null;
    }

    @Override
    protected void onStageCreated() {
        TransliterationModel transliterationModel
                = TransliterationModel.getOrCreateTransliterationModel();

        int characterIndex = 0;

        uiButtons:
        for (int row = 0; row < transliterationModel.rows(); row++) {
            for (int col = 0; col < transliterationModel.columns(); col++) {
                if (characterIndex >= transliterationModel.size())
                    break uiButtons;

                String buttonText = transliterationModel.characterAt(characterIndex);
                if (buttonText == null)
                    continue;

                Button button = new Button();
                button.setText(buttonText);
                button.setOnAction(controller);
                button.getStyleClass().add("modifiableFont");
                gridPane.add(button, col, row);

                characterIndex++;
            }
        }

        stage.sizeToScene();
    }

    @Override
    protected void onBeforeShow() {
        tbOnTop.setSelected(isAlwaysOnTop());
    }

    public void onTopChanged(ActionEvent actionEvent) {
        setAlwaysOnTop(tbOnTop.isSelected());
    }

    private class TransliterationWindowController implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String transliterationCharacter = ((Button) event.getSource()).getText();
            if (textFieldWithFocus != null)
                textFieldWithFocus.addText(transliterationCharacter);
        }
    }
}
