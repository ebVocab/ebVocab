package de.ebuchner.vocab.fx.platform;

import com.sun.javafx.font.PrismFontLoader;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontLoader {
    private static PrismFontLoader nativeFontLoader = new PrismFontLoader();

    private FontLoader() {

    }

    public static Font font(String name, FontWeight fontWeight, FontPosture fontPosture, float size) {
        return nativeFontLoader.font(
                name,
                fontWeight,
                fontPosture,
                size
        );
    }
}
