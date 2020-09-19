package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.model.nui.NuiEventListener;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;

public interface UITool extends NuiEventListener {
    String getResourceKey();

    void onOpenTool() throws Exception;

    void onContextMenuClick(TextInputControl control) throws Exception;

    void onToolCreated(NuiTextFieldWithFocus textFieldWithFocus, MenuItem menuItem);

    void onToolCreated(NuiTextFieldWithFocus textFieldWithFocus, Button button);
}
