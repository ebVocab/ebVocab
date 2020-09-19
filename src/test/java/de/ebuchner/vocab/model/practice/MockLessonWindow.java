package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.model.lessons.Lesson;
import de.ebuchner.vocab.model.lessons.LessonModel;
import de.ebuchner.vocab.model.lessons.LessonSelectionChange;
import de.ebuchner.vocab.model.lessons.LessonState;
import de.ebuchner.vocab.model.nui.NuiWindowParameter;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.nui.AbstractNuiWindow;

import java.util.ArrayList;
import java.util.List;

public class MockLessonWindow extends AbstractNuiWindow {
    private List<String> lessonNames = new ArrayList<String>();

    public void nuiWindowCreate() {
        LessonModel lessonModel = LessonModel.getOrCreateLessonModel();
        selectLessonNames(lessonModel, lessonModel.getRoot());
    }

    private void selectLessonNames(LessonModel lessonModel, Lesson lesson) {
        if (lesson.isContainer()) {
            for (Lesson child : lesson.asContainer().lessons()) {
                selectLessonNames(lessonModel, child);
            }
        } else {
            if (lessonNames.contains(lesson.getName())) {
                lessonModel.executeCommand(new LessonSelectionChange(lesson, LessonState.SELECTED));
            }
        }
    }

    public WindowType windowType() {
        return WindowType.LESSONS_WINDOW;
    }

    public void nuiWindowShow(NuiWindowParameter parameter) {
    }

    public void addLessonName(String lessonName) {
        lessonNames.add(lessonName);
    }

    public boolean attemptClosing() {
        return true;
    }
}
