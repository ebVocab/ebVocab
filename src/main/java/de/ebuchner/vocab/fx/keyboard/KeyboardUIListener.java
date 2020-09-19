package de.ebuchner.vocab.fx.keyboard;

import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

public interface KeyboardUIListener {

    void keyTyped(String characterTyped);

    void fontChanged(Font font);

    void keyCode(KeyCode keyCode);
}
