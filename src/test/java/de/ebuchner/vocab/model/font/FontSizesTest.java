package de.ebuchner.vocab.model.font;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

public class FontSizesTest extends TestCase {
    private String FONT_NAME = "fontName";

    public void testFontDecrease() {
        List<Integer> fontSizes = VocabFontSizes.fontSizes();
        VocabFont fontIn = new VocabFont(
                FONT_NAME,
                VocabFontStyle.NORMAL,
                ((Collections.max(fontSizes) - Collections.min(fontSizes)) / 2));

        VocabFont fontOut = VocabFontSizes.decreaseFont(fontIn);
        assert (fontOut.getSize() < fontIn.getSize());
        assert (fontOut.getStyle() == fontIn.getStyle());
        assert (fontOut.getName() == fontIn.getName());
    }

    public void testFontIncrease() {
        List<Integer> fontSizes = VocabFontSizes.fontSizes();
        VocabFont fontIn = new VocabFont(
                FONT_NAME,
                VocabFontStyle.NORMAL,
                ((Collections.max(fontSizes) - Collections.min(fontSizes)) / 2));

        VocabFont fontOut = VocabFontSizes.increaseFont(fontIn);
        assert (fontOut.getSize() > fontIn.getSize());
        assert (fontOut.getStyle() == fontIn.getStyle());
        assert (fontOut.getName() == fontIn.getName());
    }

    public void testFontMinMax() {
        List<Integer> fontSizes = VocabFontSizes.fontSizes();

        boolean max = false;
        for (int i = 0; i < 2; i++) {
            max = !max;
            VocabFont fontIn = new VocabFont(
                    FONT_NAME,
                    VocabFontStyle.NORMAL,
                    max ? Collections.max(fontSizes) : Collections.min(fontSizes));

            VocabFont fontOut;
            if (max)
                fontOut = VocabFontSizes.increaseFont(fontIn);
            else
                fontOut = VocabFontSizes.decreaseFont(fontIn);
            assertTrue(fontOut.getSize() == fontIn.getSize());
            assertTrue(fontOut.getStyle() == fontIn.getStyle());
            assertTrue(fontOut.getName() == fontIn.getName());
        }
    }

}
