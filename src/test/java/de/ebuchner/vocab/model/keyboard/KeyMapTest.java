package de.ebuchner.vocab.model.keyboard;

import junit.framework.TestCase;

import java.util.Arrays;

public class KeyMapTest extends TestCase {

    public void testDe() {
        KeyMap keyMap = KeyMap.fromLocale("de");
        assertEquals(48, keyMap.keyMapEntries().size());

        for (KeyMapEntry entry : keyMap.keyMapEntries()) {
            assertTrue(entry.getRow() >= 0 && entry.getRow() <= 3);
            assertTrue(entry.getColumn() >= 0 && entry.getColumn() <= 15);
            assertEquals("de", entry.getLocale());
        }

        {
            KeyMapEntry entryA = findEntry(keyMap, "A");
            assertNotNull(entryA);

            assertEquals(2, entryA.getRow());
            assertEquals(0, entryA.getColumn());
            assertTrue(entryA.isCapitalOnly());
            assertEquals("a", entryA.generatedTextForKeyModifier(KeyModifier.NORMAL));
            assertEquals("A", entryA.generatedTextForKeyModifier(KeyModifier.SHIFT));
            for (KeyMode mode : entryA.getKeyModes()) {
                assertEquals("de", mode.getLocale());
                assertTrue(KeyModifier.NORMAL.equals(mode.getModifier()) ||
                        KeyModifier.SHIFT.equals(mode.getModifier()));
            }
        }

        {
            KeyMapEntry entryTilde = findEntry(keyMap, "~");
            assertNotNull(entryTilde);

            assertEquals(1, entryTilde.getRow());
            assertEquals(11, entryTilde.getColumn());
            assertFalse(entryTilde.isCapitalOnly());
            assertEquals("+", entryTilde.generatedTextForKeyModifier(KeyModifier.NORMAL));
            assertEquals("*", entryTilde.generatedTextForKeyModifier(KeyModifier.SHIFT));
            assertEquals("~", entryTilde.generatedTextForKeyModifier(KeyModifier.ALT_GR));
            for (KeyMode mode : entryTilde.getKeyModes()) {
                assertEquals("de", mode.getLocale());
                assertTrue(KeyModifier.NORMAL.equals(mode.getModifier()) ||
                        KeyModifier.SHIFT.equals(mode.getModifier()) ||
                        KeyModifier.ALT_GR.equals(mode.getModifier()));
            }
        }
    }

    public void testAll() {
        for (String locale : Arrays.asList("de", "en", "hi", "ar")) {
            KeyMap keyMap = KeyMap.fromLocale(locale);
            assertTrue(keyMap.keyMapEntries().size() > 0);
            assertFalse(keyMap.isEmpty());
        }
    }

    public void testNotExisting() {
        assertNull(KeyMap.fromLocale("XX"));
    }

    private KeyMapEntry findEntry(KeyMap keyMap, String generatedText) {
        for (KeyMapEntry entry : keyMap.keyMapEntries()) {
            for (KeyMode keyMode : entry.getKeyModes()) {
                if (keyMode.getGenerateText().equals(generatedText))
                    return entry;
            }
        }
        return null;
    }
}
