package de.ebuchner.vocab.model.nui;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class WindowTypeTest extends TestCase {

    public void testWindowTypes() throws Exception {
        Set<String> tokens = new HashSet<String>();
        for (WindowType type : WindowType.ALL_TYPES) {
            assertNotNull(type.getToken(), type.getWindowClassName());
            assertNotNull(type.getToken(), type.getExtraKeyboard());
            assertNotNull(type.getToken(), type.getMaxInstances());

            assertNotNull(type.getToken(), type.getToken());
            assertFalse(type.getToken(), tokens.contains(type.getToken()));
            tokens.add(type.getToken());

            try {
                type.isAvailable();
            } catch (IllegalStateException e) {
                // ok config not initialized
            }
        }
    }
}
