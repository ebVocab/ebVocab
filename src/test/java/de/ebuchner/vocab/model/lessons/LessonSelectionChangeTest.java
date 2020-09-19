package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.model.commands.CompositeCommand;
import de.ebuchner.vocab.model.core.ModelChangeEvent;
import de.ebuchner.vocab.model.core.ModelCommandManagerClearedEvent;

import java.io.File;
import java.util.List;

public class LessonSelectionChangeTest extends LessonTestBase {

    private SampleLessons sampleLessons;
    private LessonModel lessonModel;

    @Override
    protected void setUp() {
        sampleLessons = new SampleLessons();
    }

    public void testRootSelectionChange() {
        lessonModel = new LessonModel(sampleLessons.getRoot());

        try {
            new LessonSelectionChange(lessonModel.getRoot(), LessonState.PARTIALLY_SELECTED);
            fail("should fail");
        } catch (IllegalArgumentException iae) {

        }
        LessonSelectionChange selectionChange = new LessonSelectionChange(lessonModel.getRoot(), LessonState.SELECTED);

        selectionChange.execute();
        assertEquals(lessonModel.getRoot().getState(), LessonState.SELECTED);
        assertAllChildren(lessonModel.getRoot(), LessonState.SELECTED);
        assertEquals(8, lessonModel.countSelectedLessons());
        assertEquals(13 /*incl. directories*/, selectionChange.getAffectedLessons().size());
        selectionChange.unExecute();
        assertEquals(lessonModel.getRoot().getState(), LessonState.UN_SELECTED);
        assertAllChildren(lessonModel.getRoot(), LessonState.UN_SELECTED);
        assertEquals(0, lessonModel.countSelectedLessons());
        assertEquals(13, selectionChange.getAffectedLessons().size());
    }

    public void testSimpleSelectionChange() {
        lessonModel = new LessonModel(sampleLessons.getRoot());

        Lesson subLesson = findLesson(lessonModel, "b-sub-1");
        assertNotNull(subLesson);

        try {
            new LessonSelectionChange(subLesson, LessonState.PARTIALLY_SELECTED);
            fail("should fail");
        } catch (IllegalArgumentException iae) {

        }

        LessonSelectionChange selectionChange = new LessonSelectionChange(subLesson, LessonState.SELECTED);
        selectionChange.execute();
        assertEquals(subLesson.getState(), LessonState.SELECTED);
        assertEquals(subLesson.getParent().getState(), LessonState.PARTIALLY_SELECTED);
        assertEquals(subLesson.getParent().getParent().getState(), LessonState.PARTIALLY_SELECTED);
        selectionChange.unExecute();
        assertEquals(lessonModel.getRoot().getState(), LessonState.UN_SELECTED);
        assertAllChildren(lessonModel.getRoot(), LessonState.UN_SELECTED);
    }

    public void testChildrenSelectionChange() {
        lessonModel = new LessonModel(sampleLessons.getRoot());
        TestListener listener = new TestListener();
        lessonModel.addListener(listener);

        Lesson subLesson1 = findLesson(lessonModel, "b-sub-1");
        assertNotNull(subLesson1);
        Lesson subLesson2 = findLesson(lessonModel, "b-sub-2");
        assertNotNull(subLesson2);
        Lesson subDir = findLesson(lessonModel, "topicBSub");
        assertNotNull(subDir);
        Lesson dir = findLesson(lessonModel, "topicB");
        assertNotNull(dir);

        lessonModel.executeCommand(new LessonSelectionChange(subLesson1, LessonState.SELECTED));
        lessonModel.executeCommand(new LessonSelectionChange(subLesson2, LessonState.SELECTED));

        assertEquals(2, listener.lessonChanged);

        assertEquals(subLesson1.getState(), LessonState.SELECTED);
        assertEquals(subLesson2.getState(), LessonState.SELECTED);
        assertEquals(subDir.getState(), LessonState.SELECTED);
        assertEquals(dir.getState(), LessonState.PARTIALLY_SELECTED);
        assertEquals(lessonModel.getRoot().getState(), LessonState.PARTIALLY_SELECTED);

        assertTrue(lessonModel.canUndo());
        lessonModel.undo();
        assertTrue(lessonModel.canUndo());
        lessonModel.undo();
        assertEquals(4, listener.lessonChanged);

        assertEquals(lessonModel.getRoot().getState(), LessonState.UN_SELECTED);
        assertAllChildren(lessonModel.getRoot(), LessonState.UN_SELECTED);
    }

    public void testPartialChange() {


        lessonModel = new LessonModel(sampleLessons.getRoot());
        CompositeCommand compositeCommand = new CompositeCommand();
        compositeCommand.getCommands().add(
                new LessonSelectionChange(
                        lessonModel.getRoot(), LessonState.SELECTED)
        );
        compositeCommand.getCommands().add(
                new LessonSelectionChange(
                        lessonModel.getRoot(), LessonState.SELECTED)
        );
        compositeCommand.getCommands().add(
                new LessonSelectionChange(
                        findLesson(lessonModel, "b-sub-1"), LessonState.UN_SELECTED)
        );
        compositeCommand.getCommands().add(
                new LessonSelectionChange(
                        findLesson(lessonModel, "b-sub-2"), LessonState.UN_SELECTED)
        );
        compositeCommand.execute();

        assertEquals(findLesson(lessonModel, "topicBSub").getState(), LessonState.UN_SELECTED);
        assertEquals(findLesson(lessonModel, "topicB").getState(), LessonState.PARTIALLY_SELECTED);
    }

    public void testToString() {
        String name = "file123";
        LessonSelectionChange change = new LessonSelectionChange(
                new Lesson(null, new File(name)),
                LessonState.UN_SELECTED
        );
        assertTrue(change.toString().contains(name));
        assertTrue(change.toString().contains(LessonState.UN_SELECTED.name()));
    }

    @Override
    protected void tearDown() {
        sampleLessons.tearDown();
    }

    class TestListener implements LessonSelectionChangeListener {
        int lessonChanged = 0;
        int cleared = 0;

        public void lessonChanged(ModelChangeEvent event, List<Lesson> affectedLessons) {
            lessonChanged++;
        }

        public void modelCommandManagerCleared(ModelCommandManagerClearedEvent event) {
            cleared++;
        }
    }
}
