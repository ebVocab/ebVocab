package de.ebuchner.vocab.fx.platform;

import de.ebuchner.vocab.fx.model.project.FxProjectBean;
import de.ebuchner.vocab.fx.nui.FxNuiDirector;
import de.ebuchner.vocab.model.font.VocabFont;
import de.ebuchner.vocab.model.font.VocabFontStyle;
import de.ebuchner.vocab.model.io.ImageEncoderBehaviour;
import de.ebuchner.vocab.model.nui.platform.UIPlatform;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import de.ebuchner.vocab.model.nui.platform.UIPlatformType;
import de.ebuchner.vocab.model.project.ProjectBean;
import de.ebuchner.vocab.nui.NuiDirector;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.text.MessageFormat;

public class FxUIPlatform implements UIPlatform {
    private FxProjectBean projectBean;
    private FxNuiDirector nuiDirector = new FxNuiDirector();

    public static FxUIPlatform instance() {
        return (FxUIPlatform) UIPlatformFactory.getUIPlatform();
    }

    public UIPlatformType getType() {
        return UIPlatformType.FX;
    }

    public void initializeUISystem() {

    }

    public ImageEncoderBehaviour newImageEncoder() {
        return new FxImgEncoder();
    }

    public String windowClassName(String windowID) {
        return MessageFormat.format(windowID, "fx");
    }

    public String uiRuntimeName() {
        String uiRuntimeName = System.getProperty("javafx.runtime.version");
        if (uiRuntimeName != null)
            return uiRuntimeName;
        throw new FxUIPlatformException("JavaFX was not proper initialized");
    }

    public NuiDirector getNuiDirector() {
        return nuiDirector;
    }

    public ProjectBean getProjectBean() {
        if (projectBean == null) {
            projectBean = new FxProjectBean();
            projectBean.loadProjectBean();
        }

        return projectBean;
    }

    public VocabFont toVocabFont(Font fxFont) {
        VocabFontStyle style = VocabFontStyle.NORMAL;

        boolean bold = false;
        boolean italic = false;

        for (String styleName : fxFont.getStyle().split("\\s")) {
            if ("italic".equalsIgnoreCase(styleName))
                italic = true;
            if ("bold".equalsIgnoreCase(styleName))
                bold = true;
        }

        if (bold && italic)
            style = VocabFontStyle.BOLD_ITALIC;
        else if (bold)
            style = VocabFontStyle.BOLD;
        else if (italic)
            style = VocabFontStyle.ITALIC;

        return new VocabFont(
                fxFont.getFamily(),
                style,
                Double.valueOf(fxFont.getSize()).intValue()
        );
    }

    public Font toFont(VocabFont vocabFont) {
        FontWeight fontWeight =
                vocabFont.getStyle() == VocabFontStyle.BOLD ||
                        vocabFont.getStyle() == VocabFontStyle.BOLD_ITALIC ?
                        FontWeight.BOLD : FontWeight.NORMAL;

        FontPosture fontPosture =
                vocabFont.getStyle() == VocabFontStyle.ITALIC ||
                        vocabFont.getStyle() == VocabFontStyle.BOLD_ITALIC ?
                        FontPosture.ITALIC : FontPosture.REGULAR;

        return FontLoader.font(
                vocabFont.getName(),
                fontWeight,
                fontPosture,
                vocabFont.getSize()
        );
    }

    public String toFxFileExtensionFilter(String extension) {
        return String.format("*.%s", extension);
    }

}
