package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;

public abstract class AbstractUITool implements UITool {

    private MenuItem myMenuItem;
    private Button myButton;

    public void onToolCreated(NuiTextFieldWithFocus textFieldWithFocus, MenuItem myMenuItem) {
        this.myMenuItem = myMenuItem;
        onFocusChangedTo(textFieldWithFocus);
    }

    @Override
    public void onToolCreated(NuiTextFieldWithFocus textFieldWithFocus, Button myButton) {
        this.myButton = myButton;
        onFocusChangedTo(textFieldWithFocus);
    }

    public NuiClosingResult onNuiWindowClosing(NuiCloseEvent event) {
        return NuiClosingResult.CLOSING_OK;
    }

    public void onNuiWindowClosed(NuiCloseEvent event) {

    }

    public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {
        if (textFieldWithFocus != null &&
                textFieldWithFocus.getText() != null &&
                textFieldWithFocus.getText().trim().length() > 0)
            disableControl(false);
        else
            disableControl(true);
    }

    public void onFocusLost() {
        disableControl(true);
    }

    private void disableControl(boolean disable) {
        if (myButton != null)
            myButton.setDisable(disable);
        if (myMenuItem != null)
            myMenuItem.setDisable(disable);
    }

    @Override
    public void onContextMenuClick(TextInputControl control) throws Exception {
        String text = control.getSelectedText();
        if (text == null || text.trim().length() == 0)
            text = control.getText();
        onAction(text);
    }

    protected abstract void onAction(String text) throws Exception;
}
