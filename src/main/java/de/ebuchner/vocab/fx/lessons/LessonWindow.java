package de.ebuchner.vocab.fx.lessons;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.lessons.*;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class LessonWindow extends FxBaseWindow {

    @FXML
    private Button btnSelectAll;
    @FXML
    private Button btnSelectNone;
    @FXML
    private Button btnFilter;
    @FXML
    private Button btnCollapseAll;

    @FXML
    private Button btnExpandAll;

    @FXML
    private Label lbRoot;
    @FXML
    private Label lbLessons;
    @FXML
    private TreeView<LessonReference> lessonTree;

    private LessonController lessonController;
    private LessonTreeUI lessonTreeUI;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.lesson.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "lesson";
    }

    @Override
    protected boolean isVocabWindowModal() {
        return true;
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        if (lessonController.onWindowClosing(closeType) == LessonController.CheckResult.CONTINUE)
            return NuiClosingResult.CLOSING_OK;

        return NuiClosingResult.CLOSING_NOT_ALLOWED;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
        lessonController.onWindowClosed(closeType);
    }

    @Override
    public WindowType windowType() {
        return WindowType.LESSONS_WINDOW;
    }

    @Override
    protected void onStageCreated() {
        this.lessonController = new LessonController(new MyBehaviour());

        lessonTree.setCellFactory(CheckBoxTreeCell.<LessonReference>forTreeView());
        this.lessonTreeUI = new LessonTreeUI(lessonTree, lessonController);

        lessonController.onWindowWasCreated();
    }

    public void onSelectAll(ActionEvent actionEvent) {
        lessonController.onSelectAll(true);
    }

    public void onSelectNone(ActionEvent actionEvent) {
        lessonController.onSelectAll(false);
    }

    public void onOk(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.OK);
    }

    public void onCancel(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CANCEL);
    }

    public void onExpandAll(ActionEvent actionEvent) {
        lessonTreeUI.expandAll();
    }

    public void onCollapseAll(ActionEvent actionEvent) {
        lessonTreeUI.collapseAll();
    }

    public void onFilter(ActionEvent actionEvent) {
        lessonController.onFilter();
    }

    class MyBehaviour implements LessonWindowBehaviour {
        public void displayRoot(File rootDirectory) {
            try {
                lbRoot.setText(rootDirectory.getCanonicalPath());
            } catch (IOException e) {
                lbRoot.setText(rootDirectory.getAbsolutePath());
            }
        }

        public void expandNonEmptyTreeRows() {
            int row = 0;
            while (true) {
                TreeItem<LessonReference> item = lessonTree.getTreeItem(row);
                if (item == null)
                    break;
                LessonReference reference = item.getValue();
                if (reference.getLessonState() != LessonState.UN_SELECTED)
                    item.setExpanded(true);
                row++;
            }
        }

        public void scrollToFirstSelectedLesson() {
            int row = lessonTreeUI.getRowOfFirstSelectedLesson();
            if (row < 0)
                return;
            lessonTree.scrollTo(row);
        }

        public void displaySelectedLessons(int availableLessons, int selectedLessons) {
            btnSelectAll.setDisable(availableLessons == 0);
            btnSelectNone.setDisable(availableLessons == 0);
            btnCollapseAll.setDisable(availableLessons == 0);
            btnExpandAll.setDisable(availableLessons == 0);
            btnFilter.setDisable(availableLessons == 0);

            if (selectedLessons > 0)
                lbLessons.setText(
                        i18n.getString(
                                "nui.lesson.tree.label.selected",
                                Collections.singletonList(selectedLessons)
                        )
                );
            else
                lbLessons.setText("-");
        }

        public void sendMessageSelectionEmpty() {

            FxDialogs.create()
                    .title(i18n.getString("nui.lesson.title"))
                    .message(i18n.getString("nui.lesson.empty.selection"))
                    .showInformation();
        }

        public boolean confirmCancel() {
            return confirmCloseTypeCancel() == NuiClosingResult.CLOSING_OK;
        }

        @Override
        public LessonFilter filterPrompt(int availableLessons) {
            while (true) {
                FxDialogs.Action action = FxDialogs.create()
                        .title(i18n.getString("nui.lesson.title"))
                        .actions(FxDialogs.ActionType.OK, FxDialogs.ActionType.CANCEL)
                        .message(i18n.getString("nui.lesson.filter.input", Collections.singletonList(availableLessons)))
                        .showTextInput();

                if (action.getActionType() != FxDialogs.ActionType.OK)
                    return null;

                String input = action.getTextInput();

                try {
                    int maxVal = Integer.parseInt(input);
                    if (maxVal >= 1 && maxVal <= availableLessons) {
                        LessonFilter filter = new LessonFilter();
                        filter.setNumberOfRecentFiles(maxVal);
                        return filter;
                    }
                } catch (NumberFormatException e) {
                    // do again
                }
            }
        }

        @Override
        public void collapseAll() {
            lessonTreeUI.collapseAll();
        }
    }
}
