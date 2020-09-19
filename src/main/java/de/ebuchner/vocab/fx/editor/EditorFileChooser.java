package de.ebuchner.vocab.fx.editor;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.fx.common.VocabFileChooser;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Collections;

public class EditorFileChooser {

    private final Window window;
    private final File preferredDirectory;
    private final I18NContext i18n = I18NLocator.locate();
    private final String extension;

    public EditorFileChooser(Window window, File preferredDirectory, String extension) {
        this.window = window;
        this.preferredDirectory = preferredDirectory;
        this.extension = extension;
    }

    private FileChooser.ExtensionFilter fileNameFilter() {
        return new FileChooser.ExtensionFilter(
                i18n.getString("nui.editor.file.filter",
                        Collections.singletonList(
                                extension
                        )),
                FxUIPlatform.instance().toFxFileExtensionFilter(extension)
        );
    }

    private VocabFileChooser fileChooser() {
        return fileChooser(null);
    }

    private VocabFileChooser fileChooser(File currentFile) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(fileNameFilter());
        if (currentFile != null && currentFile.exists() && currentFile.isFile())
            fc.setInitialDirectory(currentFile.getParentFile());
        else if (preferredDirectory != null && preferredDirectory.exists() && preferredDirectory.isDirectory())
            fc.setInitialDirectory(preferredDirectory);
        else {
            File vocabDirectory = Config.instance().getProjectInfo().getVocabDirectory();
            if (vocabDirectory != null && vocabDirectory.exists())
                fc.setInitialDirectory(vocabDirectory);
        }

        return new VocabFileChooser(fc);
    }

    public File openFileDialog() {
        VocabFileChooser fc = fileChooser();
        return fc.showOpenDialog(window);
    }

    public File saveFileDialog() {

        VocabFileChooser fc = fileChooser();
        File file = fc.showSaveDialog(window);

        if (file == null)
            return null;

        if (file.isDirectory()) {
            FxDialogs.create()
                    .message(
                            i18n.getString(
                                    "nui.editor.msg.err.dir.selection",
                                    Collections.singletonList(file.getName())
                            )
                    )
                    .showInformation();
            return null;
        }

        // append extension automatically
        if (!file.getName().endsWith(String.format(".%s", extension)))
            file = new File(file.getParent(), String.format("%s.%s", file.getName(), extension));
        return file;
    }

}
