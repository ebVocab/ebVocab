package de.ebuchner.vocab.fx.editor;

import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.fx.common.TranslateTool;
import de.ebuchner.vocab.fx.focus.FxTextFieldWithFocus;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.editor.*;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.NuiWindowParameter;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.nui.NuiDirector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.Collections;

public class EditorWindow extends FxBaseWindow {

    @FXML
    private Button btnClpCopy;
    @FXML
    private Button btnClpCut;
    @FXML
    private Button btnTranslateAll;
    @FXML
    private Button btnClpPaste;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuSearch;
    @FXML
    private Menu menuText;
    @FXML
    private Menu menuTools;
    @FXML
    private Menu menuOptions;
    @FXML
    private Menu menuHelp;
    @FXML
    private ToolBar toolBar;
    @FXML
    private Button btnFileNew;
    @FXML
    private Button btnFileOpen;
    @FXML
    private Button btnFileSave;
    @FXML
    private TableView<EditorTableRow> editorWindowTable;
    @FXML
    private Button btnEntryAdd;
    @FXML
    private Button btnEntryDelete;
    @FXML
    private Button btnEntryUp;
    @FXML
    private Button btnEntryDown;
    @FXML
    private Label lbNumSelected;
    @FXML
    private Label lbForeign;
    @FXML
    private TextField tfForeign;
    @FXML
    private Label lbType;
    @FXML
    private TextField tfType;
    @FXML
    private Label lbUser;
    @FXML
    private TextField tfUser;
    @FXML
    private Label lbComment;
    @FXML
    private TextArea tfComment;
    @FXML
    private Button btnAccept;
    @FXML
    private Button btnRevert;
    @FXML
    private MenuItem miFileSave;
    @FXML
    private MenuItem miFileRevert;
    @FXML
    private MenuItem miFileSaveAs;
    @FXML
    private TableColumn<EditorTableRow, Integer> tcRowNumber;
    @FXML
    private TableColumn<EditorTableRow, String> tcForeign;
    @FXML
    private TableColumn<EditorTableRow, String> tcType;
    @FXML
    private TableColumn<EditorTableRow, String> tcUser;
    @FXML
    private TableColumn<EditorTableRow, String> tcTranslation;

    private EditorTableUI tableUI = new EditorTableUI();

    private EditorController editorController = new EditorController(
            new MyEditorBehaviour(), new MyEntryInEditBehaviour()
    );

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.editor.title.no.file";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "editor";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        EditorController.CheckResult checkResult = editorController.onWindowClosing();
        if (checkResult == EditorController.CheckResult.CONTINUE)
            return NuiClosingResult.CLOSING_OK;
        return NuiClosingResult.CLOSING_NOT_ALLOWED;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
        editorController.onWindowClosed();
        fireOnFocusChangeToNonEditableControl();
    }

    @Override
    public WindowType windowType() {
        return WindowType.EDITOR_WINDOW;
    }

    private String titleFromFile(File currentFile) {
        String fileName;
        if (currentFile != null)
            fileName = i18n.getString(
                    "nui.editor.title.some.file",
                    Collections.singletonList(currentFile.getName()
                    )
            );
        else
            fileName = i18n.getString(vocabWindowTitleKey());

        return uiTitle("nui.editor.title", fileName);
    }

    @Override
    protected void onStageCreated() {
        tableUI.initUI(
                editorController,
                editorWindowTable,
                tcRowNumber,
                tcForeign,
                tcType,
                tcUser,
                tcTranslation
        );

        NuiDirector nuiDirector = FxUIPlatform.instance().getNuiDirector();

        lbForeign.setText(nuiDirector.uiDescriptionWithOptHint(FieldFactory.getGenericField(FieldFactory.FOREIGN)));
        tcForeign.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.FOREIGN)));

        lbType.setText(nuiDirector.uiDescriptionWithOptHint(FieldFactory.getGenericField(FieldFactory.TYPE)));
        tcType.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.TYPE)));

        lbUser.setText(nuiDirector.uiDescriptionWithOptHint(FieldFactory.getGenericField(FieldFactory.USER)));
        tcUser.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.USER)));

        lbComment.setText(nuiDirector.uiDescriptionWithOptHint(FieldFactory.getGenericField(FieldFactory.COMMENT)));

        createMenuItems(menuFile, WindowType.MenuType.FILE, editorController);
        createMenuItems(menuSearch, WindowType.MenuType.SEARCH, editorController);
        createMenuItems(menuText, WindowType.MenuType.TEXT, editorController);
        createMenuItem(menuText, TranslateTool.class);
        createMenuItems(menuTools, WindowType.MenuType.TOOLS, editorController);
        createMenuItems(menuOptions, WindowType.MenuType.OPTIONS, editorController);
        createMenuItems(menuHelp, WindowType.MenuType.HELP, editorController);

        createStandardToolbar(toolBar, editorController);

        btnEntryAdd.setDefaultButton(true);

        tfComment.focusedProperty().addListener(new EditorFocusHandler(tfComment));
        tfForeign.focusedProperty().addListener(new EditorFocusHandler(tfForeign));
        tfType.focusedProperty().addListener(new EditorFocusHandler(tfType));
        tfUser.focusedProperty().addListener(new EditorFocusHandler(tfUser));

        fireOnFocusChangeToNonEditableControl();
        editorController.onWindowWasCreated();
    }

    @Override
    protected boolean canHandleWindowParameter(NuiWindowParameter parameter) {
        if (!editorController.handleWindowParameter(parameter)) {
            windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
            return false;
        }
        return true;
    }

    @Override
    public void nuiWindowReceive(NuiWindowParameter windowParameter) {
        editorController.onReceive(windowParameter);
    }

    @Override
    public boolean canCreate(NuiWindowParameter windowParameter) {
        return editorController.onCanCreateWindow(windowParameter);
    }

    public void onFileExit(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    public void onFileNew(ActionEvent actionEvent) {
        editorController.onFileNew();
    }

    public void onFileOpen(ActionEvent actionEvent) {
        editorController.onFileOpen();
    }

    public void onFileSave(ActionEvent actionEvent) {
        editorController.onFileSave();
    }

    public void onFileSaveAs(ActionEvent actionEvent) {
        editorController.onFileSaveAs();
    }

    public void onFileRevert(ActionEvent actionEvent) {
        editorController.onFileRevert();
    }

    public void onEntryAdd(ActionEvent actionEvent) {
        editorController.onEntryAdd();
    }

    public void onEntryDelete(ActionEvent actionEvent) {
        editorController.onEntriesDelete();
    }

    public void onEntryUp(ActionEvent actionEvent) {
        editorController.onEntryMove(EditorFileModel.MoveEntryDirection.UP);
    }

    public void onEntryDown(ActionEvent actionEvent) {
        editorController.onEntryMove(EditorFileModel.MoveEntryDirection.DOWN);
    }

    public void onEntryAccept(ActionEvent actionEvent) {
        editorController.onEntryAccept();
    }

    public void onEntryRevert(ActionEvent actionEvent) {
        editorController.onEntryRevert();
    }

    private void entryToField(VocabEntry entry, TextInputControl tf, String fieldName) {
        String text = null;
        if (entry != null)
            text = entry.getFieldValue(fieldName);

        tf.setText(text);
        tf.setDisable(entry == null);
    }

    public void onClipboardCopy(ActionEvent actionEvent) {
        editorController.onClipboardCopy();
    }

    public void onTranslateAll(ActionEvent actionEvent) {
        tableUI.onTranslateAll(btnTranslateAll);
    }

    public void onClipboardCut(ActionEvent actionEvent) {
        editorController.onClipboardCut();
    }

    public void onClipboardPaste(ActionEvent actionEvent) {
        editorController.onClipboardPaste();
    }

    class EditorFocusHandler implements ChangeListener<Boolean> {

        private final TextInputControl control;

        public EditorFocusHandler(TextInputControl control) {
            this.control = control;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue)
                fireOnFocusChangedTo(new FxTextFieldWithFocus(control));
        }
    }

    class MyEditorBehaviour implements EditorWindowBehaviour {

        public EditorWindowBehaviour.AskToSaveResult askToSave() {
            FxDialogs.Action response = FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .actions(FxDialogs.ActionType.YES, FxDialogs.ActionType.NO, FxDialogs.ActionType.CANCEL)
                    .message(i18n.getString("nui.editor.msg.ask.to.save"))
                    .showConfirm();

            if (response.getActionType() == FxDialogs.ActionType.NO)
                return EditorWindowBehaviour.AskToSaveResult.DO_IGNORE;
            if (response.getActionType() == FxDialogs.ActionType.CANCEL)
                return EditorWindowBehaviour.AskToSaveResult.DO_CANCEL;

            return EditorWindowBehaviour.AskToSaveResult.DO_SAVE;
        }

        @Override
        public void sendMessageClipboardEmpty() {
            FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .message(i18n.getString("nui.editor.clipboard.empty"))
                    .showInformation();
        }

        public boolean askToRevert() {
            return FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .actions(FxDialogs.ActionType.OK, FxDialogs.ActionType.CANCEL)
                    .message(i18n.getString("nui.editor.msg.ask.to.revert"))
                    .showConfirm().getActionType() == FxDialogs.ActionType.OK;
        }

        public void removeSingleRowSelectionAndFireNoEvents() {
            tableUI.removeSingleRowSelectionAndFireNoEvents();
        }

        @Override
        public void sendMessageOpenFileFailed(Throwable t, File fileToOpen) {
            FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .message(
                            i18n.getString(
                                    "nui.editor.msg.open.failed",
                                    Collections.singletonList(fileToOpen.getName())
                            )
                    )
                    .showInformation();
        }

        public EditorFileModelBehaviour decorateModel(EditorFileModelBehaviour fileModel) {
            return tableUI.decorateModel(fileModel);
        }

        public void sendMessageFileLockFailed(File file) {
            FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .message(
                            i18n.getString(
                                    "nui.editor.msg.file.locked"
                            )
                    )
                    .showInformation();
        }

        public void sendMessageEmptyFile() {
            FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .message(
                            i18n.getString("nui.editor.msg.file.empty")
                    )
                    .showInformation();
        }

        public File openFileDialog(File preferredDirectory, String extension) {
            EditorFileChooser editorFileChooser = new EditorFileChooser(stage, preferredDirectory, extension);
            return editorFileChooser.openFileDialog();
        }

        public File saveFileDialog(File preferredDirectory, String extension) {
            EditorFileChooser editorFileChooser = new EditorFileChooser(stage, preferredDirectory, extension);
            return editorFileChooser.saveFileDialog();
        }

        public void setWindowTitle(File file) {
            stage.setTitle(titleFromFile(file));
        }

        public void setEntrySelection(EntrySelectionBehaviour entrySelection) {
            int singleSelection = -1;
            if (entrySelection.selectionCount() == 1)
                singleSelection = entrySelection.selectedModelEntryRows().iterator().next();

            btnEntryDelete.setDisable(entrySelection.selectionCount() <= 0);
            btnClpCopy.setDisable(entrySelection.selectionCount() <= 0);
            btnClpCut.setDisable(entrySelection.selectionCount() <= 0);

            btnEntryUp.setDisable(singleSelection <= 0);
            btnEntryDown.setDisable(!(singleSelection >= 0 && singleSelection < entrySelection.allEntriesCount() - 1));
            btnTranslateAll.setDisable(entrySelection.allEntriesCount() == 0 || entrySelection.selectionCount() > 0);

            lbNumSelected.setText("");
            if (entrySelection.selectionCount() > 1)
                lbNumSelected.setText(
                        i18n.getString(
                                "nui.editor.entry.selected",
                                Collections.singletonList(entrySelection.selectionCount())
                        )
                );

            tableUI.updateSelection(entrySelection);
        }

        public void setSaveOptionEnabled(boolean enabled) {
            btnFileSave.setDisable(!enabled);
            miFileSave.setDisable(!enabled);
            miFileRevert.setDisable(!enabled);
        }

        public void setSaveAsOptionEnabled(boolean enabled) {
            miFileSaveAs.setDisable(!enabled);
        }
    }

    class MyEntryInEditBehaviour implements EntryInEditWindowBehaviour {
        public VocabEntry entryFromUI() {
            VocabEntry vocabEntry = new VocabEntry();
            addFieldToEntry(vocabEntry, tfForeign, FieldFactory.FOREIGN);
            addFieldToEntry(vocabEntry, tfType, FieldFactory.TYPE);
            addFieldToEntry(vocabEntry, tfUser, FieldFactory.USER);
            addFieldToEntry(vocabEntry, tfComment, FieldFactory.COMMENT);

            return vocabEntry;
        }

        private void addFieldToEntry(VocabEntry vocabEntry, TextInputControl tf, String fieldName) {
            String value = null;
            if (tf.getText() != null && tf.getText().trim().length() > 0)
                value = tf.getText().trim();

            if (value != null && value.length() > 0)
                vocabEntry.putFieldValue(fieldName, value);

        }

        public void sendMessageFieldMissing() {
            FxDialogs.create()
                    .title(i18n.getString("nui.editor.message.title"))
                    .message(
                            i18n.getString(
                                    "nui.editor.msg.field.missing"
                            )
                    )
                    .showInformation();
        }

        public void entryToUI(VocabEntry entry) {
            entryToField(entry, tfForeign, FieldFactory.FOREIGN);
            entryToField(entry, tfType, FieldFactory.TYPE);
            entryToField(entry, tfUser, FieldFactory.USER);
            entryToField(entry, tfComment, FieldFactory.COMMENT);

            btnRevert.setDisable(entry == null);
            btnAccept.setDisable(entry == null);

            if (entry == null)
                fireOnFocusChangeToNonEditableControl();
            else {
                tfForeign.requestFocus();
                fireOnFocusChangedTo(new FxTextFieldWithFocus(tfForeign));
            }
        }
    }
}
