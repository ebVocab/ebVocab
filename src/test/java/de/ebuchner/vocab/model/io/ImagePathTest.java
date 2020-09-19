package de.ebuchner.vocab.model.io;

import junit.framework.TestCase;

public class ImagePathTest extends TestCase {

    private final static String pre = VocabIOConstants.VOCAB_IMAGE_PATH_PREFIX;
    private final static String sep = VocabIOConstants.VOCAB_IMAGE_PATH_SEPARATOR;
    private final static String ext = VocabIOConstants.VOCAB_IMAGE_PATH_SUFFIX;

    public void testSimpleNameFromPath() {
        String path = pre + sep + "a" + sep + "b" + ext;
        ImagePath name = new ImagePath(path);
        assertEquals("a", name.getEntryId());
        assertEquals("b", name.getFieldLabel());
        assertEquals(path, name.toPath());
    }

    public void testSimpleName() {
        String path = pre + sep + "a" + sep + "b" + ext;

        ImagePath name = new ImagePath("a", "b");
        assertEquals("a", name.getEntryId());
        assertEquals("b", name.getFieldLabel());
        assertEquals(path, name.toPath());
    }


}
