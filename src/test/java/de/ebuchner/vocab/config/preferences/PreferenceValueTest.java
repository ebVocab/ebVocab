package de.ebuchner.vocab.config.preferences;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceValueTest extends TestCase {

    final String TEST1 = "TEST1";
    final String TEST2 = "TEST2";

    public void testStringValue() {
        StringValue value = new StringValue(TEST1);
        assertEquals(TEST1, value.asString());

        value.fromString(TEST2);
        assertEquals(TEST2, value.asString());
    }

    public void testIntValue() {
        IntegerValue value = new IntegerValue(17);
        assertEquals("17", value.asString());
        assert 17 == value.getValue();

        value.fromString("18");
        assertEquals("18", value.asString());
        assertEquals(18, (int) value.getValue());
    }

    public void testListValue() {
        List<String> test = Arrays.asList("rani", "mukherji");
        StringListValue value = new StringListValue(test);

        String s = value.asString();

        StringListValue value2 = new StringListValue();
        value2.fromString(s);

        assertEquals(test, value2.getStrings());
    }

    public void testEmptyMapValue() {
        Map<String, String> aMap = new HashMap<String, String>();
        StringMapValue value = new StringMapValue(aMap);
        String s = value.asString();

        StringMapValue value2 = new StringMapValue();
        value2.fromString(s);

        assertEquals(aMap.size(), value2.getStringMap().size());
    }

    public void testSomeMapValue() {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("a", "aValue");
        aMap.put("b", "bValue");
        aMap.put("cx", "cxValue");

        StringMapValue value = new StringMapValue(aMap);
        String s = value.asString();

        StringMapValue value2 = new StringMapValue();
        value2.fromString(s);

        assertEquals(aMap.size(), value2.getStringMap().size());

        assertEquals("aValue", value2.getStringMap().get("a"));
        assertEquals("bValue", value2.getStringMap().get("b"));
        assertEquals("cxValue", value2.getStringMap().get("cx"));
    }

}
