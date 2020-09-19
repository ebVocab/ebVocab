package de.ebuchner.vocab.tools;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class RandomUtilTest extends TestCase {
    public void testNextId() {
        Set<String> ids = new HashSet<String>();

        for (int i = 0; i < 1000; i++) {
            assertTrue(ids.add(RandomTools.nextId()));
        }
    }
}
