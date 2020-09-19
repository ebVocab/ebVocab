package de.ebuchner.vocab.model.font;

import de.ebuchner.vocab.config.preferences.Preferences;
import junit.framework.TestCase;

import java.io.File;

public class FontValueTest extends TestCase {

    public void testIO() {
        File file = new File(System.getProperty("java.io.tmpdir"), FontValueTest.class.getName() + ".preferences");
        Preferences preferences1 = new Preferences(file);

        FontValue fontValue1 = new FontValue(new VocabFont("font", VocabFontStyle.BOLD, 17));
        preferences1.getPreferenceValueList().putName("font", fontValue1);

        preferences1.savePreferences();

        Preferences preferences2 = new Preferences(file);

        assertNotNull(preferences2.getPreferenceValueList().getName("font"));
        assertEquals(preferences2.getPreferenceValueList().getName("font").getClass(), FontValue.class);
        FontValue fontValue2 = (FontValue) preferences2.getPreferenceValueList().getName("font");
        assertEquals(fontValue2.getFont(), fontValue1.getFont());
    }

}
