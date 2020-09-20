package de.ebuchner.vocab.fx.platform;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontLoader {
    private FontLoader() {

    }

    public static Font font(String name, FontWeight fontWeight, FontPosture fontPosture, float size) {
        return Font.font(
                name,
                fontWeight,
                fontPosture,
                size
        );
    }
}
