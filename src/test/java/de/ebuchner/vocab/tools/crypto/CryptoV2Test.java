package de.ebuchner.vocab.tools.crypto;

import junit.framework.TestCase;

public class CryptoV2Test extends TestCase {

    public void testStringToByte() throws Exception {
        assertArrayToString("Test1710");
        assertArrayToString("x-Äöüéçß@€µ\n\f\b\t");
        assertArrayToString("");
    }

    private void assertArrayToString(String test) {
        CryptoV2 cryptoV2 = new CryptoV2();
        final String ref = cryptoV2.encrypt(test);

        assertEquals(ref, cryptoV2.byteArrayToString(cryptoV2.stringToByteArray(ref)));
    }
}
