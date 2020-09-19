package de.ebuchner.vocab.fx.font;

import de.ebuchner.vocab.fx.platform.FontLoader;
import de.ebuchner.vocab.model.font.FontModel;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class FontCellFactory
        implements Callback<ListView<String>, ListCell<String>> {

    String fontSample = FontModel.getOrCreateFontModel().getFontSample();

    public static void customize(ComboBox<String> comboBox) {
        FontCellFactory factory = new FontCellFactory();
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.new FontCell());
    }

    @Override
    public ListCell<String> call(ListView<String> param) {
        return new FontCell();
    }

    class FontCell extends ListCell<String> {

        private Font font = null;

        public FontCell() {
            setContentDisplay(ContentDisplay.LEFT);
        }

        @Override
        protected void updateItem(String fontName, boolean empty) {
            super.updateItem(fontName, empty);
            if (fontName == null) {
                setText("");
                return;
            }

            if (font == null) {
                font = FontLoader.font(
                        fontName,
                        FontWeight.NORMAL,
                        FontPosture.REGULAR,
                        Double.valueOf(getFont().getSize()).floatValue()
                );

            }
            setFont(font);
            setText(String.format("%s - %s", fontName, fontSample));
        }


    }

}
