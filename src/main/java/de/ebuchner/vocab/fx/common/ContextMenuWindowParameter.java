package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.model.nui.NuiWindowParameter;
import javafx.scene.control.TextInputControl;

public class ContextMenuWindowParameter implements NuiWindowParameter {

    private final TextInputControl textField;

    public ContextMenuWindowParameter(TextInputControl textField) {
        this.textField = textField;
    }

    @Override
    public String getToken() {
        return "context";
    }

    public String getContextFieldValue() {
        return textField.getText();
    }
}
