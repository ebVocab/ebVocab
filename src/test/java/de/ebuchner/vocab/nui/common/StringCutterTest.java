package de.ebuchner.vocab.nui.common;

import junit.framework.TestCase;

public class StringCutterTest extends TestCase {

    private static final int MAX_LEN = 10;

    public void testEnsureLength() {
        assertNull(StringCutter.ensureLength(null, MAX_LEN));
        assertEquals("", StringCutter.ensureLength("", MAX_LEN));
        assertEquals("0123456789", StringCutter.ensureLength("0123456789", MAX_LEN));
        assertEquals("01...6789A", StringCutter.ensureLength("0123456789A", MAX_LEN));
    }

}
