package de.ebuchner.vocab.model.lessons;

import junit.framework.TestCase;

public abstract class LessonTestBase extends TestCase {

    protected void assertAllChildren(Lesson lesson, LessonState state) {
        assertEquals(lesson.getName() + " should be " + state.name(), lesson.getState(), state);
        if (lesson.isContainer()) {
            for (Lesson child : lesson.asContainer().lessons()) {
                assertAllChildren(child, state);
            }
        }
    }

    protected Lesson findLesson(LessonModel model, String name) {
        LessonContainer container = model.getRoot();
        return findLesson(container, name);
    }

    protected Lesson findLesson(Lesson lesson, String name) {
        if (lesson.getName().equals(name))
            return lesson;
        if (!lesson.isContainer())
            return null;

        for (Lesson child : lesson.asContainer().lessons()) {
            Lesson result = findLesson(child, name);
            if (result != null)
                return result;
        }
        return null;
    }

}
