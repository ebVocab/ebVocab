package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.config.VocabLanguage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class SupportedLanguageCellFactory
        extends AbstractProjectCellFactory
        implements Callback<ListView<VocabLanguage>, ListCell<VocabLanguage>> {

    public static void customize(ComboBox<VocabLanguage> comboBox) {
        SupportedLanguageCellFactory factory = new SupportedLanguageCellFactory();
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.new SupportedLanguageCell());
    }

    @Override
    public ListCell<VocabLanguage> call(ListView<VocabLanguage> param) {
        return new SupportedLanguageCell();
    }

    class SupportedLanguageCell extends ListCell<VocabLanguage> {

        public SupportedLanguageCell() {
            setContentDisplay(ContentDisplay.LEFT);
        }

        @Override
        protected void updateItem(VocabLanguage vocabLanguage, boolean empty) {
            super.updateItem(vocabLanguage, empty);
            if (vocabLanguage == null) {
                setGraphic(null);
                setText("");
                return;
            }

            setText(vocabLanguage.getDisplayName());
            Image image = imageOf(vocabLanguage);
            if (image != null)
                setGraphic(new ImageView(image));
        }


    }
}
