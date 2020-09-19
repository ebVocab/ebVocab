package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.model.practice.FieldRendererContext;
import de.ebuchner.vocab.nui.common.I18NLocator;
import de.ebuchner.vocab.nui.common.StringCutter;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DefaultFieldRenderer implements FieldRenderer {

    private static final Color NORMAL_LABEL = Color.BLACK;
    private static final Color MARKED_LABEL = Color.BLUE;
    private static final int VISIBLE_TEXT_LEN = 40;
    private I18NContext i18n = I18NLocator.locate();

    public void renderLabel(FieldRendererContext context, Label label) {
        if (context.isFieldHidden() && context.isValueNotEmpty())
            label.setTextFill(MARKED_LABEL);
        else
            label.setTextFill(NORMAL_LABEL);
    }

    public void renderTextComponent(FieldRendererContext context, TextInputControl textComponent) {
        textComponent.setText(context.getValue());
    }

    public String lessonComponentText(File fileRef) {
        if (fileRef == null) {
            return "";
        }

        String text;
        try {
            text = i18n.getString(
                    "nui.practice.lesson.file.info",
                    Arrays.asList(
                            fileRef.getName(),
                            fileRef.getParentFile().getCanonicalPath()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return StringCutter.ensureLength(text, VISIBLE_TEXT_LEN * 3 / 2);
    }

    public void renderLessonComponent(File fileRef, TextInputControl textComponent) {
        textComponent.setText(lessonComponentText(fileRef));
    }

}
