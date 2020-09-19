package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import junit.framework.TestCase;

public class VocabEntryTest extends TestCase {
    public void testUpdateFieldsFrom() throws Exception {
        VocabEntry me = new VocabEntry();
        VocabEntry other = new VocabEntry();

        assertFalse(me.updateFieldsFrom(other));
        assertEquals(me.fieldCount(), other.fieldCount());
        assertEquals(0, me.fieldCount());

        me.putFieldValue("A", "A");
        other.putFieldValue("A", "A");

        me.putFieldValue("B", "B");
        other.putFieldValue("B", "B");

        assertFalse(me.updateFieldsFrom(other));
        assertEquals(me.fieldCount(), other.fieldCount());

        other.putFieldValue("C", "C");
        assertTrue(me.updateFieldsFrom(other));
        assertEquals(me.fieldCount(), other.fieldCount());

        assertEquals(me.getFieldValue("C"), "C");

        me.putFieldValue("D", "D");
        assertTrue(me.updateFieldsFrom(other));
        assertEquals(me.fieldCount(), other.fieldCount());
        assertNull(me.getFieldValue("D"));
    }
}
