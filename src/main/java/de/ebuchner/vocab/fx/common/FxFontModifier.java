package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.fx.platform.FontLoader;
import de.ebuchner.vocab.model.font.FontModel;
import de.ebuchner.vocab.model.font.VocabFont;
import de.ebuchner.vocab.model.font.VocabFontStyle;
import de.ebuchner.vocab.model.font.VocabFontType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FxFontModifier {

    private static final String MODIFIABLE_FONT_STYLE_CLASS = "modifiableFont";

    private FxFontModifier() {

    }

    public static void modifyFontInUI(int extraHeight, Parent sceneParent) {
        modifyFontInUI(
                extraHeight,
                FontModel.getOrCreateFontModel().getFont(VocabFontType.VOCABULARY),
                sceneParent
        );
    }

    private static void modifyFontInUI(int extraHeight, VocabFont font, Node sceneNode) {
        if (sceneNode instanceof TextInputControl)
            applyFont(extraHeight, font, (TextInputControl) sceneNode);
        if (sceneNode instanceof Parent) {
            for (Node childNode : ((Parent) sceneNode).getChildrenUnmodifiable())
                modifyFontInUI(extraHeight, font, childNode);
        }
    }

    private static void applyFont(int extraHeight, VocabFont font, TextInputControl node) {
        if (!isModifiableFontNode(node))
            return;

        if (font == null) {
            node.setStyle(null);
            return;
        }

        Font fxFont = FontLoader.font(
                font.getName(),
                font.getStyle() == VocabFontStyle.BOLD || font.getStyle() == VocabFontStyle.BOLD_ITALIC
                        ? FontWeight.BOLD : FontWeight.NORMAL,
                font.getStyle() == VocabFontStyle.ITALIC || font.getStyle() == VocabFontStyle.BOLD_ITALIC
                        ? FontPosture.ITALIC : FontPosture.REGULAR,
                font.getSize()
        );
        if (fxFont == null)
            return;

        node.setFont(fxFont);
        if (extraHeight > 0)
            node.setPrefSize(node.getPrefWidth(), node.getPrefHeight() + extraHeight);

    }

    private static boolean isModifiableFontNode(Node node) {
        for (String styleClass : node.getStyleClass()) {
            if (MODIFIABLE_FONT_STYLE_CLASS.equals(styleClass)) {
                return true;
            }
        }
        return false;
    }
}