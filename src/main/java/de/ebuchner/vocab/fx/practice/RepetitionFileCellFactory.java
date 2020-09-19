package de.ebuchner.vocab.fx.practice;

import de.ebuchner.vocab.fx.project.AbstractProjectCellFactory;
import de.ebuchner.vocab.nui.common.RepetitionItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class RepetitionFileCellFactory extends AbstractProjectCellFactory
        implements Callback<ListView<RepetitionItem>, ListCell<RepetitionItem>> {

    public static void customize(ListView<RepetitionItem> listView) {
        RepetitionFileCellFactory factory = new RepetitionFileCellFactory();
        listView.setCellFactory(factory);
    }

    @Override
    public ListCell<RepetitionItem> call(ListView<RepetitionItem> param) {
        return new RepetitionFile();
    }

    class RepetitionFile extends ListCell<RepetitionItem> {

        public RepetitionFile() {
            setContentDisplay(ContentDisplay.LEFT);
        }

        @Override
        protected void updateItem(RepetitionItem repetitionItem, boolean empty) {
            super.updateItem(repetitionItem, empty);
            if (repetitionItem == null)
                return;
            setText(repetitionItem.toString());
        }


    }
}