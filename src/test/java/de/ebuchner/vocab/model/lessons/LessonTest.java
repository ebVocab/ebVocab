package de.ebuchner.vocab.model.lessons;

import junit.framework.TestCase;

import java.io.File;

public class LessonTest extends TestCase {

    public void testEquals() {
        Lesson lessonA = new Lesson(null, new File("fileA"));
        Lesson lessonA1 = new Lesson(null, new File("fileA"));
        Lesson lessonB = new Lesson(null, new File("fileB"));

        assertEquals(lessonA, lessonA1);
        assertFalse(lessonA.equals(lessonB));

        lessonA1.setState(LessonState.UN_SELECTED);
        lessonA.setState(LessonState.PARTIALLY_SELECTED);
        assertEquals(lessonA, lessonA1);

    }

    public void testContainer() {
        LessonContainer parent = new LessonContainer(new File("x"));
        assertTrue(parent.isContainer());
        assertEquals(parent, parent.asContainer());

        Lesson lesson = new Lesson(parent, new File("y"));
        assertFalse(lesson.isContainer());
        try {
            lesson.asContainer();
        } catch (UnsupportedOperationException e) {

        }

    }

    public void testCtor() {
        try {
            new Lesson(null, null);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            new Lesson(new LessonContainer(new File("x")), null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        new Lesson(null, new File("y"));
    }

    public void testToString() {
        final String name = "name xyz";
        assertNotNull(new Lesson(null, new File(name)).toString());
        assertTrue(new Lesson(null, new File(name)).toString().contains(name));
    }
}
