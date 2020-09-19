package de.ebuchner.vocab.model.practice;

import junit.framework.TestCase;

public class PracticeProgressTest extends TestCase {

    public void testEmpty() {
        PracticeProgress practiceProgress = new PracticeProgress();
        assertTrue(practiceProgress.isEmpty());

        assertTrue(PracticeProgress.EMPTY.isEmpty());
    }

    public void testNonEmpty() {
        {
            PracticeProgress practiceProgress = new PracticeProgress(1, 4, 1, false, 0, 0, false);
            assertFalse(practiceProgress.isEmpty());
            assertEquals(1, practiceProgress.getNumberOfLessons());
            assertEquals(4, practiceProgress.getTotalNumberOfEntries());
            assertEquals(1, practiceProgress.getCurrentPosition());
            assertEquals("50.0%", practiceProgress.getRatioCurrentOfTotalText());
        }

        try {
            new PracticeProgress(0, 3, 1, false, 0, 0, false);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            new PracticeProgress(1, 1, 1, false, 0, 0, false);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            new PracticeProgress(1, 4, -1, false, 0, 0, false);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }
}
