package de.ebuchner.vocab.model.keyboard;

import junit.framework.TestCase;

public class KeyboardAvailableTest extends TestCase {
    public void testIsAvailable() throws Exception {
        assertNotNull(new KeyboardAvailable().keyMapURL("hi"));
        assertNull(new KeyboardAvailable().keyMapURL("xx"));
    }
}
