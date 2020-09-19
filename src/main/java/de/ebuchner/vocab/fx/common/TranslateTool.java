package de.ebuchner.vocab.fx.common;

import de.ebuchner.vocab.config.VocabEnvironment;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import de.ebuchner.vocab.model.translate.TranslateURL;

public class TranslateTool extends AbstractUITool {

    public String getResourceKey() {
        return "nui.menu.tools.translate";
    }

    public void onOpenTool() throws Exception {
        NuiTextFieldWithFocus textField = UIPlatformFactory.getUIPlatform().getNuiDirector().getLastTextFieldWithFocus();
        if (textField == null)
            return;

        onAction(textField.getText());
    }

    protected void onAction(String text) throws Exception {
        if (text == null || text.trim().length() == 0)
            return;

        if (VocabEnvironment.osType() == VocabEnvironment.OSType.WINDOWS)
            Runtime.getRuntime().exec(
                    String.format(
                            "cmd /c start \"\" %s",
                            TranslateURL.url(text)
                    )
            );
    }
}
