package de.ebuchner.vocab.config.preferences;

import junit.framework.TestCase;

public class PasswordValueTest extends TestCase {

    public void testPasswordValue() {
        String secret = "secret";

        // created in code
        PasswordValue passwordValueOut = new PasswordValue();
        passwordValueOut.setValueFromUnencrypted(secret);

        // stored on disc
        assertFalse(secret.equals(passwordValueOut.asString()));

        // restored from disk
        PasswordValue passwordValueIn = new PasswordValue();
        passwordValueIn.fromString(passwordValueOut.asString());
        assertEquals(passwordValueOut.asString(), passwordValueIn.asString());
        assertFalse(secret.equals(passwordValueIn.asString()));

        // read password in code
        assertEquals(secret, passwordValueIn.asUnencryptedString());
    }

}
