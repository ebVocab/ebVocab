package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VocabEntryRefListPreferenceValueTest extends TestCase {

    public void testRefPrefValue() {
        List<VocabEntryRef> listOut = new ArrayList<VocabEntryRef>();

        for (int listSize = 0; listSize < 5; listSize++) {
            listOut.clear();
            for (int i = 0; i < listSize; i++) {
                listOut.add(new VocabEntryRef(new File(String.valueOf(i)), "id" + i));
            }

            VocabEntryRefListPreferenceValue valOut = new VocabEntryRefListPreferenceValue();
            valOut.getVocabEntryRefList().addAll(listOut);

            String s = valOut.asString();

            VocabEntryRefListPreferenceValue valIn = new VocabEntryRefListPreferenceValue();
            valIn.fromString(s);

            List<VocabEntryRef> listIn = valIn.getVocabEntryRefList();
            assertNotNull(listIn);
            assertEquals(listSize, listIn.size());
            for (int i = 0; i < listSize; i++) {
                VocabEntryRef refOut = listOut.get(i);
                VocabEntryRef refIn = listIn.get(i);
                assertEquals(refOut, refIn);
            }
        }
    }
}
