package de.ebuchner.vocab.fx.keyboard;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.nui.focus.FocusAware;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

public class KeyboardWindow extends FxBaseWindow implements FocusAware {
    private static final String KEYBOARD_INACTIVE_ICON = "/de/ebuchner/vocab/nui/res/keyboard_inactive.png";
    private static final String KEYBOARD_ACTIVE_ICON = "/de/ebuchner/vocab/nui/res/keyboard_active.png";

    @FXML
    private Canvas cvKeyboard;
    @FXML
    private Label lbKeyboardActive;
    @FXML
    private ToggleButton tbOnTop;

    private KeyboardWindowController controller = new KeyboardWindowController();
    private NuiTextFieldWithFocus textFieldWithFocus;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.keyboard.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "keyboard";
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
        return WindowType.KEYBOARD_WINDOW;
    }

    @Override
    protected void onBeforeShow() {
        tbOnTop.setSelected(isAlwaysOnTop());
    }

    @Override
    protected void onStageCreated() {
        KeyboardUI keyboardUI = new KeyboardUI(cvKeyboard);
        keyboardUI.addListener(controller);

        String keyboardName = keyboardUI.keyboardName();
        if (keyboardName != null)
            stage.setTitle(uiTitleNoResKey(keyboardName));

        cvKeyboard.requestFocus();
        stage.sizeToScene();
    }

    public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {
        if (!textFieldWithFocus.isEditable())
            return;

        this.textFieldWithFocus = textFieldWithFocus;
        lbKeyboardActive.setGraphic(
                new ImageView(
                        new Image(KEYBOARD_ACTIVE_ICON)
                )
        );
    }

    public void onFocusLost() {
        this.textFieldWithFocus = null;
        lbKeyboardActive.setGraphic(
                new ImageView(
                        new Image(KEYBOARD_INACTIVE_ICON)
                )
        );
    }

    public void onTopChanged(ActionEvent actionEvent) {
        setAlwaysOnTop(tbOnTop.isSelected());
    }

    private class KeyboardWindowController implements KeyboardUIListener {

        public void keyTyped(String characterTyped) {
            if (textFieldWithFocus != null)
                textFieldWithFocus.addText(characterTyped);
        }

        public void fontChanged(Font font) {
            stage.sizeToScene();
        }

        public void keyCode(KeyCode keyCode) {
            if (textFieldWithFocus == null)
                return;
            if (keyCode != KeyCode.BACK_SPACE)
                return;
            textFieldWithFocus.onKeyBackspace();
        }
    }
}
