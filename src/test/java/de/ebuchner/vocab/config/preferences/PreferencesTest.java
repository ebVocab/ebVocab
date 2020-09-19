package de.ebuchner.vocab.config.preferences;

import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;

public class PreferencesTest extends TestCase {

    public void testIO() {
        File file = new File(System.getProperty("java.io.tmpdir"), PreferencesTest.class.getName() + ".preferences");
        Preferences preferences1 = new Preferences(file);

        StringValue stringValue1 = new StringValue("stringValue");
        preferences1.getPreferenceValueList().putName("string", stringValue1);
        IntegerValue integerValue1 = new IntegerValue(4711);
        preferences1.getPreferenceValueList().putName("int", integerValue1);
        StringListValue listValue1 = new StringListValue(Arrays.asList("a", "b", "c"));
        preferences1.getPreferenceValueList().putName("list", listValue1);
        BooleanValue booleanValue1 = new BooleanValue(true);
        preferences1.getPreferenceValueList().putName("boolean", booleanValue1);

        StringMapValue stringMapValue1 = new StringMapValue();
        stringMapValue1.getStringMap().put("key1", "value1");
        stringMapValue1.getStringMap().put("key2", "value2");
        preferences1.getPreferenceValueList().putName("map", stringMapValue1);

        preferences1.savePreferences();

        Preferences preferences2 = new Preferences(file);

        assertNotNull(preferences2.getPreferenceValueList().getName("string"));
        assertEquals(preferences2.getPreferenceValueList().getName("string").getClass(), StringValue.class);
        StringValue stringValue2 = (StringValue) preferences2.getPreferenceValueList().getName("string");
        assertEquals(stringValue2.getString(), stringValue1.getString());

        assertNotNull(preferences2.getPreferenceValueList().getName("int"));
        assertEquals(preferences2.getPreferenceValueList().getName("int").getClass(), IntegerValue.class);
        IntegerValue integerValue2 = (IntegerValue) preferences2.getPreferenceValueList().getName("int");
        assertEquals(integerValue2.getValue(), integerValue1.getValue());

        assertNotNull(preferences2.getPreferenceValueList().getName("list"));
        assertEquals(preferences2.getPreferenceValueList().getName("list").getClass(), StringListValue.class);
        StringListValue listValue2 = (StringListValue) preferences2.getPreferenceValueList().getName("list");
        assertEquals(listValue2.getStrings(), listValue1.getStrings());

        assertNotNull(preferences2.getPreferenceValueList().getName("boolean"));
        assertEquals(preferences2.getPreferenceValueList().getName("boolean").getClass(), BooleanValue.class);
        BooleanValue booleanValue2 = (BooleanValue) preferences2.getPreferenceValueList().getName("boolean");
        assertEquals(booleanValue2.getValue(), booleanValue1.getValue());

        assertNotNull(preferences2.getPreferenceValueList().getName("map"));
        StringMapValue stringMapValue2 = (StringMapValue) preferences2.getPreferenceValueList().getName("map");
        assertEquals(2, stringMapValue2.getStringMap().size());
        assertEquals("value1", stringMapValue2.getStringMap().get("key1"));
        assertEquals("value2", stringMapValue2.getStringMap().get("key2"));
    }

}
