package de.ebuchner.vocab.model.translator;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Translator {

    private final Translate translate;

    public Translator() {
        this.translate = TranslateOptions.getDefaultInstance().getService();
    }

    public String translate(String fromLocale, String toLocale, String text) {

        Translation translation =
                translate.translate(
                        text,
                        Translate.TranslateOption.sourceLanguage(fromLocale),
                        Translate.TranslateOption.targetLanguage(toLocale));

        return translation.getTranslatedText();
    }
}
