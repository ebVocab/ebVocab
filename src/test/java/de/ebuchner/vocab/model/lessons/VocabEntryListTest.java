package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.io.File;

public class VocabEntryListTest extends TestCase {

    public void testEntryList() {
        VocabEntryList vocabEntryList = new VocabEntryList();
        assertEquals(0, vocabEntryList.entryCount());
        try {
            vocabEntryList.getEntry(0);
            fail();
        } catch (IllegalArgumentException e) {

        }

        VocabEntry vocabEntry1 = new VocabEntry();
        VocabEntry vocabEntry2 = new VocabEntry(vocabEntry1.getId());
        VocabEntry vocabEntry3 = new VocabEntry();
        VocabEntryRef entryRef3 = VocabEntryRef.fromEntry((File) null, vocabEntry3);

        vocabEntryList.addEntry(vocabEntry1);
        try {
            vocabEntryList.addEntry(vocabEntry2);
            fail();
        } catch (IllegalArgumentException e) {

        }
        vocabEntryList.addEntry(vocabEntry3);

        assertSame(vocabEntry1, vocabEntryList.getEntry(0));
        assertSame(vocabEntry3, vocabEntryList.getEntry(1));

        assertSame(vocabEntry3, vocabEntryList.findEntry(entryRef3));

        int pos = 0;
        for (VocabEntry x : vocabEntryList.entries()) {
            if (pos == 0)
                assertSame(vocabEntry1, x);
            else if (pos == 1)
                assertSame(vocabEntry3, x);
            else
                fail("Unexpected element " + x);
            pos++;
        }
    }

    public void testFindEntry() {
        VocabEntryList vocabEntryList = new VocabEntryList();

        VocabEntry entry1 = new VocabEntry();
        VocabEntryRef ref1 = VocabEntryRef.fromEntry((File) null, entry1);
        vocabEntryList.addEntry(entry1);

        VocabEntry entry2 = new VocabEntry();
        VocabEntryRef ref2 = VocabEntryRef.fromEntry((File) null, entry2);
        vocabEntryList.addEntry(entry2);

        VocabEntry entryOther = new VocabEntry();
        VocabEntryRef refOther = VocabEntryRef.fromEntry((File) null, entryOther);


        assertEquals(entry1, vocabEntryList.findEntry(ref1));
        assertEquals(entry2, vocabEntryList.findEntry(ref2));
        assertNull(vocabEntryList.findEntry(refOther));


    }
}
