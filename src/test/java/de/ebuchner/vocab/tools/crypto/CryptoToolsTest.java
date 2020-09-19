package de.ebuchner.vocab.tools.crypto;

import junit.framework.TestCase;

public class CryptoToolsTest extends TestCase {

    public void testCompatibility() {
        String secret = "default";
        CryptoTools cryptoTools = CryptoTools.getInstance();
        String decrypted = cryptoTools.decrypt(secret);
        assertEquals(decrypted, secret);

        String reEncrypted = cryptoTools.encrypt(secret);
        assertFalse(reEncrypted.equals(secret));
        assertTrue(reEncrypted, reEncrypted.startsWith(String.format("{{%d}}", cryptoTools.version())));

        String reDecrypted = cryptoTools.decrypt(reEncrypted);
        assertEquals(secret, reDecrypted);
    }
}
