package de.ebuchner.vocab.fx.font;

import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AvailableFontsModel {

    private List<String> allFontNames = new ArrayList<>();
    private List<String> fontNames = new ArrayList<>();

    public AvailableFontsModel() {
        allFontNames.addAll(Font.getFamilies());
        fontNames.addAll(allFontNames);
    }

    public void doFilter(String text) {
        List<String> filteredFontNames = new ArrayList<>();
        for (String fontName : allFontNames) {
            // Font font = new Font(fontName, 8);
            //if (font.canDisplayUpTo(text) == -1)
            filteredFontNames.add(fontName);
        }

        fontNames.clear();
        fontNames.addAll(filteredFontNames.stream().collect(Collectors.toList()));
    }

    public void doUnFilter() {
        fontNames.clear();
        fontNames.addAll(allFontNames.stream().collect(Collectors.toList()));
    }

    public Collection<String> getFontNames() {
        return fontNames;
    }
}
