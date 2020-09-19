package de.ebuchner.vocab.nui;

import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.nui.WindowTypeFilter;
import junit.framework.TestCase;

public class DefaultWindowTypeFilterTest extends TestCase {

    public void testPracticeWindowFilter() {
        WindowTypeFilter filter = new DefaultWindowTypeFilter(WindowType.PRACTICE_WINDOW);
        assertTrue(filter.accept(WindowType.EDITOR_WINDOW));
        assertFalse(filter.accept(WindowType.KEYBOARD_WINDOW));
        assertFalse(filter.accept(WindowType.PRACTICE_WINDOW));
        assertTrue(filter.accept(WindowType.ALPHABET_WINDOW));
    }

    public void testEditorWindowFilter() {
        WindowTypeFilter filter = new DefaultWindowTypeFilter(WindowType.EDITOR_WINDOW);
        assertFalse(filter.accept(WindowType.EDITOR_WINDOW));
        assertTrue(filter.accept(WindowType.KEYBOARD_WINDOW));
        assertTrue(filter.accept(WindowType.PRACTICE_WINDOW));
        assertTrue(filter.accept(WindowType.ALPHABET_WINDOW));
    }

    public void testFontWindowFilter() {
        WindowTypeFilter filter = new DefaultWindowTypeFilter(WindowType.FONT_SELECTION_WINDOW);
        assertTrue(filter.accept(WindowType.EDITOR_WINDOW));
        assertFalse(filter.accept(WindowType.KEYBOARD_WINDOW));
        assertTrue(filter.accept(WindowType.PRACTICE_WINDOW));
        assertTrue(filter.accept(WindowType.ALPHABET_WINDOW));
    }
}
