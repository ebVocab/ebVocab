package de.ebuchner.vocab.fx.editor;

import de.ebuchner.vocab.config.ProjectConfig;
import de.ebuchner.vocab.fx.common.ProgressDialog;
import de.ebuchner.vocab.model.editor.EditorController;
import de.ebuchner.vocab.model.editor.EditorFileModel;
import de.ebuchner.vocab.model.editor.EditorFileModelBehaviour;
import de.ebuchner.vocab.model.editor.EntrySelectionBehaviour;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.translator.Translator;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class EditorTableUI {
    private TableView<EditorTableRow> tableView;
    private final Busy busy = new Busy();

    public void initUI(
            EditorController editorController,
            TableView<EditorTableRow> tableView,
            TableColumn<EditorTableRow, Integer> tcRowNumber,
            TableColumn<EditorTableRow, String> tcForeign,
            TableColumn<EditorTableRow, String> tcType,
            TableColumn<EditorTableRow, String> tcUser,
            TableColumn<EditorTableRow, String> tcTranslation) {
        this.tableView = tableView;

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tcRowNumber.setCellValueFactory(
                new PropertyValueFactory<>("rowNumber")
        );
        tcForeign.setCellValueFactory(
                new PropertyValueFactory<>("foreign")
        );
        tcType.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );
        tcUser.setCellValueFactory(
                new PropertyValueFactory<>("user")
        );
        tcTranslation.setCellValueFactory(
                new PropertyValueFactory<>("translation")
        );

        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<EditorTableRow>) c -> {
            if (busy.isBusy())
                return;

            busy.enterBusy();
            try {
                List<VocabEntry> selectedEntries =
                        c.getList().stream().map(EditorTableRow::getEntry).collect(Collectors.toList());

                editorController.onTableSelectionChanged(
                        selectedEntries
                );
            } finally {
                busy.leaveBusy();
            }
        });
    }


    public EditorFileModelBehaviour decorateModel(EditorFileModelBehaviour fileModel) {
        return new ModelDecorator(fileModel);
    }

    public void updateSelection(EntrySelectionBehaviour entrySelection) {
        if (busy.isBusy())
            return;

        busy.enterBusy();
        try {
            tableView.getSelectionModel().clearSelection();
            int firstRow = -1;
            for (int row : entrySelection.selectedModelEntryRows()) {
                firstRow = row;
                tableView.getSelectionModel().selectIndices(row);
            }
            if (firstRow >= 0)
                tableView.scrollTo(firstRow);
        } finally {
            busy.leaveBusy();
        }
    }

    public void removeSingleRowSelectionAndFireNoEvents() {
        if (busy.isBusy())
            return;

        busy.enterBusy();
        try {
            if (tableView.getSelectionModel().getSelectedIndices().size() != 1)
                return;

            tableView.getSelectionModel().clearSelection();
        } finally {
            busy.leaveBusy();
        }
    }

    public void onTranslateAll(Button btnTranslateAll) {
        if (busy.isBusy())
            return;

        busy.enterBusy();
        btnTranslateAll.setDisable(true);

        try {
            List<EditorTableRow> rows = tableView.getItems();
            final Translator translator = new Translator();

            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    int cnt = 0;
                    for (EditorTableRow row : rows) {
                        row.setTranslation(translator.translate(ProjectConfig.instance().getLocale(), ProjectConfig.instance().getSystemLocale(), row.getForeign()));
                        updateProgress(cnt++, rows.size());
                    }
                    updateProgress(10, 10);
                    return null;
                }
            };
            ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.addTask(task);

            task.setOnSucceeded(event -> {
                btnTranslateAll.setDisable(false);
                tableView.refresh();
            });

            progressDialog.getStage().show();
            new Thread(task).start();
        } finally {
            busy.leaveBusy();
        }
    }

    private static class Busy {
        private int count = 0;

        public void enterBusy() {
            count++;
        }

        public boolean isBusy() {
            return count > 0;
        }

        public void leaveBusy() {
            count--;
        }
    }

    private class ModelDecorator implements EditorFileModelBehaviour {
        final EditorFileModelBehaviour fileModel;

        public ModelDecorator(EditorFileModelBehaviour fileModel) {
            this.fileModel = fileModel;
        }

        private void onEditorFileModelChanged() {
            if (busy.isBusy())
                return;

            busy.enterBusy();
            try {
                tableView.getItems().clear();
                int entryCount = fileModel.entryCount();
                for (int i = 0; i < entryCount; i++) {
                    VocabEntry entry = fileModel.getEntry(i);
                    tableView.getItems().add(new EditorTableRow(new EditorTableRowData(i + 1, entry)));
                }
            } finally {
                busy.leaveBusy();
            }
        }

        private void onEditorEntryChanged(int rowNumber, VocabEntry modifiedEntry) {
            if (busy.isBusy())
                return;

            busy.enterBusy();
            try {
                // modifying EditorTableRow is not enough - tableView listens to items, not to EditorTableRow
                tableView.getItems().set(rowNumber - 1, new EditorTableRow(new EditorTableRowData(rowNumber, modifiedEntry)));
            } finally {
                busy.leaveBusy();
            }
        }

        // EditorFileModelBehaviour
        public void addEntry(VocabEntry entry) {
            fileModel.addEntry(entry);
            onEditorFileModelChanged();
            tableView.scrollTo(tableView.getItems().size() - 1);
        }

        private void scrollTo(VocabEntry entry) {
        }

        // EditorFileModelBehaviour
        public File getFile() {
            return fileModel.getFile();
        }

        // EditorFileModelBehaviour
        public void openFile(File file) {
            fileModel.openFile(file);
            onEditorFileModelChanged();
        }

        // EditorFileModelBehaviour
        public boolean hasFile() {
            return fileModel.hasFile();
        }

        // EditorFileModelBehaviour
        public void clearFile() {
            fileModel.clearFile();
            onEditorFileModelChanged();
        }

        // EditorFileModelBehaviour
        public boolean isDirty() {
            return fileModel.isDirty();
        }

        // EditorFileModelBehaviour
        public boolean isEmpty() {
            return fileModel.isEmpty();
        }

        // EditorFileModelBehaviour
        public void saveFile(File file) {
            fileModel.saveFile(file);
            onEditorFileModelChanged();
        }

        // EditorFileModelBehaviour
        public VocabEntry updateEntry(String id, VocabEntry entry) {
            VocabEntry modelEntry = fileModel.updateEntry(id, entry);
            onEditorEntryChanged(fileModel.indexOf(modelEntry) + 1, modelEntry);
            return modelEntry;
        }

        // EditorFileModelBehaviour
        public int entryCount() {
            return fileModel.entryCount();
        }

        // EditorFileModelBehaviour
        public VocabEntry getEntry(int pos) {
            return fileModel.getEntry(pos);
        }

        // EditorFileModelBehaviour
        public void deleteEntries(List<VocabEntry> entries) {
            fileModel.deleteEntries(entries);
            onEditorFileModelChanged();
        }

        // EditorFileModelBehaviour
        public void moveEntry(VocabEntry entry, EditorFileModel.MoveEntryDirection direction) {
            fileModel.moveEntry(entry, direction);
            onEditorFileModelChanged();
        }

        // EditorFileModelBehaviour
        public int indexOf(VocabEntry entry) {
            return fileModel.indexOf(entry);
        }

        // EditorFileModelBehaviour
        public VocabEntry findEntry(String id) {
            return fileModel.findEntry(id);
        }

    }
}
