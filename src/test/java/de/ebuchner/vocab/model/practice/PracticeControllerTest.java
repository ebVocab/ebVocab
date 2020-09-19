package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.config.fields.Field;
import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.fx.nui.FxNuiDirector;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.VocabModel;
import de.ebuchner.vocab.model.lessons.RepetitionMode;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.nui.NuiStarter;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class PracticeControllerTest extends TestCase {
    private MockLessonWindow mockLessonWindow;

    @Override
    protected void setUp() {
        new NuiStarter().prepareProjectDir(NuiStarter.PreferencesHandling.RESET);

        this.mockLessonWindow = new MockLessonWindow();
        getNuiDirector().putMockWindow(WindowType.LESSONS_WINDOW, mockLessonWindow);
    }

    private FxNuiDirector getNuiDirector() {
        return (FxNuiDirector) FxUIPlatform.instance().getNuiDirector();
    }

    public void testEmptyData() {
        SimplePracticeWindow window = new SimplePracticeWindow();
        PracticeController controller = new PracticeController(window);
        controller.onWindowWasCreated();

        for (Field field : window.getFieldsEnabled().keySet()) {
            assertFalse("not enabled " + field.name(), window.getFieldsEnabled().get(field));
        }
        assertFalse(window.isLessonFieldsEnabled());
        assertNull(window.getLessonFieldFileRef());
        assertFalse(window.isLessonFieldShow());

        assertFalse(window.isNavigationForwardEnabled());
        assertFalse(window.isNavigationBackwardEnabled());
        assertFalse(window.isStrategyOptionsEnabled());
        assertFalse(window.isReverseOptionEnabled());
    }

    public void testSingleLesson() {
        // open lesson
        mockLessonWindow.addLessonName("01 - Quick Vocab");

        // start screen (ask next)
        SimplePracticeWindow window = new SimplePracticeWindow();
        PracticeController controller = new PracticeController(window);
        controller.onWindowWasCreated();

        // check screen
        for (Field field : window.getFieldsEnabled().keySet()) {
            assertTrue(window.getFieldsEnabled().get(field));
        }
        assertTrue(window.isLessonFieldsEnabled());
        assertNull(window.getLessonFieldFileRef());
        assertFalse(window.isLessonFieldShow());

        assertTrue(window.isNavigationForwardEnabled());
        assertFalse(window.isNavigationBackwardEnabled());

        assertTrue(window.isStrategyOptionsEnabled());
        assertEquals(SelectedStrategy.RANDOM, window.getSelectedStrategy());

        assertTrue(window.isReverseOptionEnabled());
        assertEquals(PracticeReverse.NORMAL, window.getReverseOption());

        // navigate forward (show solution)
        controller.onNavigationForward();

        // check screen
        for (Field field : window.getFieldsEnabled().keySet()) {
            assertTrue(window.getFieldsEnabled().get(field));
        }
        assertTrue(window.isLessonFieldsEnabled());
        assertNotNull(window.getLessonFieldFileRef());
        assertFalse(window.isLessonFieldShow());

        assertTrue(window.isNavigationForwardEnabled());
        assertFalse(window.isNavigationBackwardEnabled());

        assertTrue(window.isStrategyOptionsEnabled());
        assertEquals(SelectedStrategy.RANDOM, window.getSelectedStrategy());

        assertTrue(window.isReverseOptionEnabled());
        assertEquals(PracticeReverse.NORMAL, window.getReverseOption());

        // navigate forward (ask next) -> navigation backward becomes available
        controller.onNavigationForward();

        for (Field field : window.getFieldsEnabled().keySet()) {
            assertTrue(window.getFieldsEnabled().get(field));
        }
        assertTrue(window.isLessonFieldsEnabled());
        assertNull(window.getLessonFieldFileRef());
        assertFalse(window.isLessonFieldShow());

        assertTrue(window.isNavigationForwardEnabled());
        assertTrue(window.isNavigationBackwardEnabled());

        assertTrue(window.isStrategyOptionsEnabled());
        assertEquals(SelectedStrategy.RANDOM, window.getSelectedStrategy());

        assertTrue(window.isReverseOptionEnabled());
        assertEquals(PracticeReverse.NORMAL, window.getReverseOption());

        // navigate backward (still ask next) -> navigation backward becomes disabled
        controller.onNavigationBackward();

        for (Field field : window.getFieldsEnabled().keySet()) {
            assertTrue(window.getFieldsEnabled().get(field));
        }
        assertTrue(window.isLessonFieldsEnabled());
        assertNull(window.getLessonFieldFileRef());
        assertFalse(window.isLessonFieldShow());

        assertTrue(window.isNavigationForwardEnabled());
        assertFalse(window.isNavigationBackwardEnabled());

        assertTrue(window.isStrategyOptionsEnabled());
        assertEquals(SelectedStrategy.RANDOM, window.getSelectedStrategy());

        assertTrue(window.isReverseOptionEnabled());
        assertEquals(PracticeReverse.NORMAL, window.getReverseOption());
    }

    public void testRepetition() {
        // open lesson
        mockLessonWindow.addLessonName("01 - Quick Vocab");

        // start screen (ask next)
        SimplePracticeWindow window = new SimplePracticeWindow();
        PracticeController controller = new PracticeController(window);
        controller.onWindowWasCreated();

        // duplicate state of repetition controls
        assertEquals(RepetitionMode.OFF, window.getRepetitionMode());
        assertTrue(window.isRepetitionAddEnabled());
        assertTrue(window.isRepetitionAddVisible());
        assertFalse(window.isRepetitionRemoveEnabled());
        assertFalse(window.isRepetitionRemoveVisible());
        assertFalse(window.isRepetitionClearEnabled());
        assertFalse(window.isRepetitionClearVisible());
        assertEquals(0, window.getRepetitionCount());

        Map<String, FieldRendererContext> rendererContextBefore = new HashMap<String, FieldRendererContext>();
        rendererContextBefore.putAll(window.getRenderedFieldMap());

        // add first item to repetition
        controller.onRepetitionAdd();

        Map<String, FieldRendererContext> rendererContextAfter = new HashMap<String, FieldRendererContext>();
        rendererContextAfter.putAll(window.getRenderedFieldMap());

        assertEqualsContext(rendererContextBefore, rendererContextAfter);

        // duplicate state of repetition controls
        assertEquals(RepetitionMode.OFF, window.getRepetitionMode());
        assertFalse(window.isRepetitionAddEnabled());
        assertFalse(window.isRepetitionAddVisible());
        assertTrue(window.isRepetitionRemoveEnabled());
        assertTrue(window.isRepetitionRemoveVisible());
        assertTrue(window.isRepetitionClearEnabled());
        assertTrue(window.isRepetitionClearVisible());
        assertEquals(1, window.getRepetitionCount());
    }

    private void assertEqualsContext(
            Map<String, FieldRendererContext> contextRef,
            Map<String, FieldRendererContext> contextLog
    ) {
        assertEquals(contextRef.size(), contextLog.size());
        for (String keyRef : contextRef.keySet()) {
            assertTrue("key not found " + keyRef, contextLog.containsKey(keyRef));
            FieldRendererContext ref = contextRef.get(keyRef);
            FieldRendererContext log = contextRef.get(keyRef);

            assertEquals(ref.getFileRef(), log.getFileRef());
            assertEquals(ref.getField().name(), log.getField().name());
            assertEquals(ref.getValue(), log.getValue());
            assertEquals(ref.isFieldHidden(), log.isFieldHidden());
            assertEquals(ref.isValueNotEmpty(), log.isValueNotEmpty());
        }
    }

    public void testLessonField() {
        // open lesson
        mockLessonWindow.addLessonName("01 - Quick Vocab");

        // start screen (ask next)
        SimplePracticeWindow window = new SimplePracticeWindow();
        PracticeController controller = new PracticeController(window);
        controller.onWindowWasCreated();

        assertNull(window.getLessonFieldFileRef());
        assertTrue(window.isLessonFieldsEnabled());
        assertFalse(window.isLessonFieldShow());

        // activate lesson
        controller.onLessonShowOptionChanged(true);

        // duplicate lesson fields
        assertNotNull(window.getLessonFieldFileRef());
        assertTrue(window.isLessonFieldsEnabled());
        assertTrue(window.isLessonFieldShow());

        // goto show solution
        controller.onNavigationForward();

        // duplicate lesson fields
        assertNotNull(window.getLessonFieldFileRef());
        assertTrue(window.isLessonFieldsEnabled());
        assertTrue(window.isLessonFieldShow());

        // deactivate lesson
        controller.onLessonShowOptionChanged(false);

        // duplicate lesson fields
        assertNotNull(window.getLessonFieldFileRef());
        assertTrue(window.isLessonFieldsEnabled());
        assertFalse(window.isLessonFieldShow());
    }

    public void testFieldContent() {
        // open lesson
        mockLessonWindow.addLessonName("01 - Quick Vocab");

        // start screen (ask next)
        SimplePracticeWindow window = new SimplePracticeWindow();
        PracticeController controller = new PracticeController(window);
        controller.onWindowWasCreated();

        // duplicate fields contents
        assertFieldVisible(FieldFactory.FOREIGN, true, window);
        assertFieldVisible(FieldFactory.USER, false, window);

        // next -> show solution
        controller.onNavigationForward();

        // duplicate fields contents
        assertFieldVisible(FieldFactory.FOREIGN, true, window);
        assertFieldVisible(FieldFactory.USER, true, window);

        // next -> ask next
        controller.onNavigationForward();

        // duplicate fields contents
        assertFieldVisible(FieldFactory.FOREIGN, true, window);
        assertFieldVisible(FieldFactory.USER, false, window);

        // switch to reverse
        controller.onReverseOptionChanged(true);

        // duplicate fields contents
        assertFieldVisible(FieldFactory.FOREIGN, false, window);
        assertFieldVisible(FieldFactory.USER, true, window);

        // switch to normal again
        controller.onReverseOptionChanged(false);

        // duplicate fields contents
        assertFieldVisible(FieldFactory.FOREIGN, true, window);
        assertFieldVisible(FieldFactory.USER, false, window);
    }

    private void assertFieldVisible(String fieldName, boolean visible, SimplePracticeWindow window) {
        FieldRendererContext context = window.getRenderedFieldMap().get(fieldName);
        assertNotNull(context);
        assertNotNull(context.getValue());
        assertEquals(!visible, context.isFieldHidden());
        assertTrue(context.isValueNotEmpty());

    }

    @Override
    protected void tearDown() {
        getNuiDirector().removeMockWindow(WindowType.LESSONS_WINDOW);
        VocabModel.getInstance().shutDown();
        Config.instance().dispose();
    }

}
