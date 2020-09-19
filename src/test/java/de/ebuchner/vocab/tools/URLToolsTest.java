package de.ebuchner.vocab.tools;

import junit.framework.TestCase;

public class URLToolsTest extends TestCase {

    public void testUrlTools() {
        assertEquals(
                "0123456789",
                URLTools.asText(URLToolsTest.class.getResource("/de/ebuchner/vocab/tools/urlTest.txt"))
        );
    }

}
