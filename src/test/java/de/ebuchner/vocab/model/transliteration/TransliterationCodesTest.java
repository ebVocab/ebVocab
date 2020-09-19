package de.ebuchner.vocab.model.transliteration;

import junit.framework.TestCase;

public class TransliterationCodesTest extends TestCase {

    public void testCodes() {
        TransliterationCodes codes = new TransliterationCodes();

        assertEquals("Some text", codes.replaceCodes("Some text"));
        assertEquals(null, codes.replaceCodes(null));
        assertEquals("", codes.replaceCodes(""));
        assertTrue(codes.replaceCodes("e${TILDE}"), codes.replaceCodes("e${TILDE}").length() == 2);
        assertTrue(codes.replaceCodes("e${TILDE}"), codes.replaceCodes("e${TILDE}").indexOf("TILDE") < 0);
    }
}
