package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class VocabFileChooser {

    private final FileChooser fileChooser;
    private I18NContext i18n = I18NLocator.locate();

    public VocabFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    public File showOpenDialog(Window parent) {
        while (true) {
            File file = fileChooser.showOpenDialog(parent);
            if (file == null)
                return null;

            if (file.exists())
                return file;

            FxDialogs.create()
                    .message(
                            i18n.getString(
                                    "nui.file.not.existing"
                            )
                    )
                    .showInformation();
        }
    }

    public File showSaveDialog(Window parent) {
        return fileChooser.showSaveDialog(parent);
    }
}
