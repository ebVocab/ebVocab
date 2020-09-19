package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.model.cloud.ProjectItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ProjectItemCellFactory
        extends AbstractProjectCellFactory
        implements Callback<ListView<ProjectItem>, ListCell<ProjectItem>> {

    public static void customize(ComboBox<ProjectItem> comboBox) {
        ProjectItemCellFactory factory = new ProjectItemCellFactory();
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.new ProjectItemCell());
    }

    @Override
    public ListCell<ProjectItem> call(ListView<ProjectItem> param) {
        return new ProjectItemCell();
    }

    class ProjectItemCell extends ListCell<ProjectItem> {

        @Override
        protected void updateItem(ProjectItem projectItem, boolean empty) {
            super.updateItem(projectItem, empty);
            if (projectItem == null) {
                setText("");
                return;
            }

            setText(projectItem.getProjectName());
        }


    }
}