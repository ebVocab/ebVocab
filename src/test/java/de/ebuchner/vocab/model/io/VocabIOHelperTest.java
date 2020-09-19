package de.ebuchner.vocab.model.io;

import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import de.ebuchner.vocab.nui.NuiStarter;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VocabIOHelperTest extends TestCase {

    private static final String FILE_REF_D = "fileRef%d";

    @Override
    protected void setUp() {
        new NuiStarter().prepareProjectDir();
    }

    @Test
    public void testRefIO() throws IOException {
        List<VocabEntryRef> entryRefs = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            VocabEntryRef ref = new VocabEntryRef(new File(String.format(FILE_REF_D, i)), String.valueOf(i));
            entryRefs.add(ref);
        }

        File testFile = File.createTempFile("VocabIOHelperTest", ".xml");
        System.out.println(testFile.getCanonicalPath());
        VocabIOHelper.toRefFile(testFile, entryRefs);

        List<VocabEntryRef> entryRefs2 = VocabIOHelper.fromRefFile(testFile);
        assertNotNull(entryRefs2);
        assertEquals(entryRefs.size(), entryRefs2.size());
        for (int i = 1; i <= entryRefs2.size(); i++) {
            VocabEntryRef ref = entryRefs2.get(i - 1);
            assertEquals(String.valueOf(i), ref.getId());
            assertEquals(
                    new File(String.format(FILE_REF_D, i)).getName(),
                    ref.getFileRef().getName()
            );
        }
    }
}