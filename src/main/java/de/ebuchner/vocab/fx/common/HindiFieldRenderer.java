package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.config.templates.hi.MasculineFemininePlural;
import de.ebuchner.vocab.model.practice.FieldRendererContext;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.scene.control.TextInputControl;

import java.util.Arrays;
import java.util.Collections;

public class HindiFieldRenderer extends DefaultFieldRenderer {

    private static final String COLOR_STYLE_FEMININE = "practice-type-feminine";
    private static final String COLOR_STYLE_MASCULINE = "practice-type-masculine";

    I18NContext i18n = I18NLocator.locate();

    @Override
    public void renderTextComponent(FieldRendererContext context, TextInputControl textComponent) {

        textComponent.getStyleClass().removeAll(Arrays.asList(
                COLOR_STYLE_FEMININE, COLOR_STYLE_MASCULINE
        ));

        if (context.isFieldHidden()) {
            super.renderTextComponent(context, textComponent);
            return;
        }

        if (FieldFactory.TYPE.equals(context.getField().name())) {
            String foreignValue = context.getCurrentEntry().getFieldValue(FieldFactory.FOREIGN);
            MasculineFemininePlural plural = new MasculineFemininePlural();

            String value = context.getValue();
            if (plural.isMasculine(value)) {
                textComponent.setText(i18n.getString(
                        "nui.practice.hindi.gender.m",
                        Collections.singletonList(plural.doMasculine(foreignValue))));
                textComponent.getStyleClass().add(COLOR_STYLE_MASCULINE);
                return;
            } else if (plural.isFeminine(value)) {
                textComponent.setText(i18n.getString(
                        "nui.practice.hindi.gender.f",
                        Collections.singletonList(plural.doFeminine(foreignValue))));
                textComponent.getStyleClass().add(COLOR_STYLE_FEMININE);
                return;
            }
        }

        super.renderTextComponent(context, textComponent);
    }
}
