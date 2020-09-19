package de.ebuchner.vocab.model.io;

import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.config.ProjectConfig;
import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.tools.RandomTools;
import junit.framework.TestCase;

import java.io.File;

public class VocabEncoderTest extends TestCase {

    public void testEmptyList() {
        assertEncodingOk(new VocabEntryList());
    }

    public void testNonEmptyList() {
        VocabEntryList entries = new VocabEntryList();

        VocabEntry entry1 = createTestEntry();
        entry1.putFieldValue(FieldFactory.FOREIGN, "Hedgehog");
        entry1.putFieldValue(FieldFactory.USER, "Igel");
        entries.addEntry(entry1);

        VocabEntry entry2 = createTestEntry();
        entry2.putFieldValue(FieldFactory.FOREIGN, "Bear");
        entry2.putFieldValue(FieldFactory.USER, "Bär");
        entry2.putFieldValue(FieldFactory.COMMENT, "Nutztier");
        entries.addEntry(entry2);

        assertEncodingOk(entries);
    }

    private VocabEntryList assertEncodingOk(VocabEntryList entriesOut) {
        File testFile = writeToFile(entriesOut);

        VocabEntryList entriesIn = readFromFile(testFile);

        try {
            System.out.println("Reading from " + testFile.getCanonicalPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEntriesEquals(entriesOut, entriesIn);

        return entriesIn;
    }

    private void assertEntriesEquals(VocabEntryList entriesOut, VocabEntryList entriesIn) {
        assertNotNull(entriesOut);
        assertNotNull(entriesIn);
        assertEquals(entriesOut.entryCount(), entriesIn.entryCount());

        for (int pos = 0; pos < entriesIn.entryCount(); pos++) {
            VocabEntry entryOut = entriesOut.getEntry(pos);
            VocabEntry entryIn = entriesIn.getEntry(pos);

            assertEquals(entryOut.getId(), entryIn.getId());
            assertEquals(entryOut.fieldCount(), entryIn.fieldCount());
            for (String fieldName : entryOut.fieldNames()) {
                assertNotNull(entryIn.getFieldValue(fieldName));
                assertEquals(entryOut.getFieldValue(fieldName), entryIn.getFieldValue(fieldName));
            }
        }
    }

    private File writeToFile(VocabEntryList entries) {
        File testFile;
        try {
            testFile = File.createTempFile(VocabEncoderTest.class.getName(), ConfigConstants.FILE_EXTENSION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        VocabIOHelper.toFile(testFile, entries);
        return testFile;
    }


    private VocabEntryList readFromFile(File file) {
        return VocabIOHelper.fromFile(file);
    }


    public void testLinefeed() {
        final String KEY = FieldFactory.USER;

        String newLine = System.getProperty("line.separator");

        VocabEntry entry = createTestEntry();
        entry.putFieldValue(KEY, "This is a multiline text" + newLine +
                "second line" + newLine +
                "third line");
        VocabEntryList entries = new VocabEntryList();
        entries.addEntry(entry);

        assertEncodingOk(entries);
    }

    private VocabEntry createTestEntry() {
        VocabEntry entry = new VocabEntry(RandomTools.nextId());
        return entry;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        ProjectConfig.instance().dispose();
    }

}
