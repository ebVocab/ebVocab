package de.ebuchner.vocab.fx.lessons;

import de.ebuchner.vocab.model.core.ModelChangeEvent;
import de.ebuchner.vocab.model.core.ModelCommandManagerClearedEvent;
import de.ebuchner.vocab.model.lessons.*;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonTreeUI {

    private final TreeView<LessonReference> treeControl;
    private final LessonController lessonController;
    private final LessonTreeUIController lessonTreeUIController;

    private Map<Lesson, CheckBoxTreeItem<LessonReference>> treeItemMap = new HashMap<>();

    public LessonTreeUI(TreeView<LessonReference> treeControl, LessonController lessonController) {
        this.treeControl = treeControl;
        this.lessonController = lessonController;
        this.lessonTreeUIController = new LessonTreeUIController();

        LessonModelBehaviour lessonModelBehaviour = lessonController.getLessonModelBehaviour();

        LessonReference rootRef = lessonModelBehaviour.getRootReference();

        CheckBoxTreeItem<LessonReference> rootItem =
                createTreeItem(rootRef);

        treeControl.setRoot(rootItem);

        addChildren(rootItem, rootRef);

        lessonModelBehaviour.addListener(lessonTreeUIController);
    }

    private void addChildren(CheckBoxTreeItem<LessonReference> parent, LessonReference parentRef) {
        if (!parentRef.getLesson().isContainer())
            return;

        for (Lesson child : parentRef.getLesson().asContainer().lessons()) {
            LessonReference childRef = new LessonReference(child);
            CheckBoxTreeItem<LessonReference> childItem = createTreeItem(childRef);
            parent.getChildren().add(childItem);
            addChildren(childItem, childRef);
        }

    }

    private CheckBoxTreeItem<LessonReference> createTreeItem(LessonReference lessonRef) {
        CheckBoxTreeItem<LessonReference> childItem =
                new CheckBoxTreeItem<>(lessonRef);
        treeItemMap.put(lessonRef.getLesson(), childItem);

        applyState(childItem, lessonRef.getLessonState());

        childItem.setIndependent(true);
        childItem.addEventHandler(
                CheckBoxTreeItem.checkBoxSelectionChangedEvent(),
                lessonTreeUIController
        );

        if (lessonRef.getLesson().isContainer()) {
            String imgUrl = "/de/ebuchner/vocab/fx/lessons/folder_yellow.png";
            childItem.setGraphic(
                    new ImageView(
                            new Image(imgUrl)
                    ));
        }

        return childItem;
    }

    private void applyState(CheckBoxTreeItem<LessonReference> item, LessonState state) {
        // generate no events. Only events from user should be handled
        lessonTreeUIController.deaf = true;
        try {
            item.setIndeterminate(false);
            item.setSelected(false);

            if (state == LessonState.PARTIALLY_SELECTED)
                item.setIndeterminate(true);
            else if (state == LessonState.SELECTED)
                item.setSelected(true);
        } finally {
            lessonTreeUIController.deaf = false;
        }
    }

    public int getRowOfFirstSelectedLesson() {
        for (int row = 0; ; row++) {
            CheckBoxTreeItem<LessonReference> item = (CheckBoxTreeItem<LessonReference>) treeControl.getTreeItem(row);
            if (item == null)
                break;
            LessonReference reference = item.getValue();

            if (reference.getLessonState() == LessonState.SELECTED && !reference.getLesson().isContainer())
                return row;
        }
        return -1;
    }

    public void expandAll() {
        if (treeControl.getRoot() == null)
            return;

        expandAll(treeControl.getRoot());
    }

    private void expandAll(TreeItem<LessonReference> node) {
        node.setExpanded(true);
        node.getChildren().forEach(this::expandAll);
    }

    public void collapseAll() {
        if (treeControl.getRoot() == null)
            return;

        collapseAll(treeControl.getRoot());
    }

    private void collapseAll(TreeItem<LessonReference> node) {
        node.setExpanded(false);
        node.getChildren().forEach(this::collapseAll);
    }

    private class LessonTreeUIController implements LessonSelectionChangeListener,
            EventHandler<CheckBoxTreeItem.TreeModificationEvent<LessonReference>> {

        private boolean deaf = false;

        @Override
        public void handle(CheckBoxTreeItem.TreeModificationEvent<LessonReference> event) {
            if (deaf)
                return;

            CheckBoxTreeItem<LessonReference> treeItem = event.getTreeItem();

            treeItem.getValue().setLessonState(
                    treeItem.isSelected() ?
                            LessonState.SELECTED :
                            LessonState.UN_SELECTED
            );
            lessonController.onLessonSelectionChanged(treeItem.getValue());
        }

        @Override
        public void lessonChanged(ModelChangeEvent event, List<Lesson> affectedLessons) {
            for (Lesson affectedLesson : affectedLessons) {
                TreeItem<LessonReference> treeItem = treeItemMap.get(affectedLesson);
                if (treeItem == null)
                    return;

                CheckBoxTreeItem<LessonReference> item = (CheckBoxTreeItem<LessonReference>) treeItem;
                applyState(item, affectedLesson.getState());
            }
        }

        @Override
        public void modelCommandManagerCleared(ModelCommandManagerClearedEvent event) {

        }
    }
}
