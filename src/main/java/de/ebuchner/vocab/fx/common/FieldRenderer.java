package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.model.practice.FieldRendererContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.io.File;

public interface FieldRenderer {

    void renderLabel(FieldRendererContext context, Label label);

    void renderTextComponent(FieldRendererContext context, TextInputControl textComponent);

    void renderLessonComponent(File fileRef, TextInputControl textComponent);

    String lessonComponentText(File fileRef);
}