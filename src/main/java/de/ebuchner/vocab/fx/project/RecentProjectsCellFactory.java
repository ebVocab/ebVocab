package de.ebuchner.vocab.fx.project;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.model.project.ProjectConfiguration;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class RecentProjectsCellFactory
        extends AbstractProjectCellFactory
        implements Callback<ListView<File>, ListCell<File>> {

    private I18NContext i18n = I18NLocator.locate();

    public static void customize(ComboBox<File> comboBox) {
        RecentProjectsCellFactory factory = new RecentProjectsCellFactory();
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.new RecentProjectsCell());
    }

    @Override
    public ListCell<File> call(ListView<File> param) {
        return new RecentProjectsCell();
    }

    class RecentProjectsCell extends ListCell<File> {

        public RecentProjectsCell() {
            setContentDisplay(ContentDisplay.LEFT);
        }

        @Override
        protected void updateItem(File file, boolean empty) {
            super.updateItem(file, empty);

            if (file == null) {
                setGraphic(null);
                setText("");
                return;
            }

            try {
                setText(i18n.getString("nui.project.recent.item",
                        Arrays.asList(
                                file.getName(),
                                file.getParentFile().getCanonicalPath()
                        )
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String projectLocale = ProjectConfiguration.projectLocale(file);
            Image image = imageOf(projectLocale);
            if (image != null)
                setGraphic(new ImageView(image));
        }


    }

}
