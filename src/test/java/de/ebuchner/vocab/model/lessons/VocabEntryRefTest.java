package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.io.File;

public class VocabEntryRefTest extends TestCase {

    public void testRef() {
        File fileRef = new File("some file");
        Lesson lesson = new Lesson(null, fileRef);
        VocabEntry entry = new VocabEntry();

        VocabEntryRef entryRef = new VocabEntryRef(lesson.getFileRef(), entry.getId());
        assertEquals(entryRef.getFileRef(), fileRef);
        assertEquals(entryRef.getId(), entry.getId());

        VocabEntryRef entryRef2 = VocabEntryRef.fromEntry(lesson, entry);
        assertEquals(entryRef2.getFileRef(), fileRef);
        assertEquals(entryRef2.getId(), entry.getId());
    }

    public void testEquality() {
        final String idOne = "id1";
        final String idOther = "id2";
        VocabEntryRef ref1 = new VocabEntryRef(new File("ref1"), idOne);
        VocabEntryRef ref2 = new VocabEntryRef(new File("ref2"), idOther);

        File ref = new File("ref");
        VocabEntryRef ref3 = new VocabEntryRef(ref, idOne);
        VocabEntryRef ref4 = new VocabEntryRef(ref, idOther);

        assertEquals(ref1, ref3);
        assertFalse(ref1.equals(ref2));
        assertFalse(ref3.equals(ref2));
        assertFalse(ref4.equals(ref3));
    }
}
