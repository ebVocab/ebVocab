package de.ebuchner.vocab.fx.focus;

import de.ebuchner.vocab.nui.focus.AbstractTextFieldWithFocus;
import javafx.scene.control.TextInputControl;

public class FxTextFieldWithFocus extends AbstractTextFieldWithFocus {
    private final TextInputControl textField;

    public FxTextFieldWithFocus(TextInputControl textField) {
        this.textField = textField;
    }

    @Override
    public int getCaretPosition() {
        return textField.getCaretPosition();
    }

    @Override
    public void setCaretPosition(int caretPosition) {
        textField.positionCaret(caretPosition);
    }

    @Override
    public int getSelectionStart() {
        return textField.getSelection().getStart();
    }

    @Override
    public int getSelectionEnd() {
        return textField.getSelection().getEnd();
    }

    @Override
    protected void changeText(String text) {
        textField.setText(text);
    }

    @Override
    public String getText() {
        return textField.getText();
    }

    @Override
    public boolean isEditable() {
        return !textField.isDisabled() && textField.isEditable();
    }
}
