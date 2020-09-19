package de.ebuchner.vocab.model.practice;

import junit.framework.TestCase;

public class PracticeStatisticsValueTest extends TestCase {

    public void testIncrement() {
        PracticeStatisticsValue value = new PracticeStatisticsValue();
        assertEquals(0, value.getUsageCount());

        long timeStamp0 = value.getTimestamp();
        assertEquals(PracticeStatisticsValue.EMPTY_TIMESTAMP, timeStamp0);

        value.incrementUsage();
        assertEquals(1, value.getUsageCount());
        long timeStamp1 = value.getTimestamp();
        assertTrue(timeStamp1 != PracticeStatisticsValue.EMPTY_TIMESTAMP);
        assertTrue(timeStamp1 > 0);

        // same day, don't increment
        value.incrementUsage();
        assertEquals(1, value.getUsageCount());
        long timeStamp2 = value.getTimestamp();
        assertEquals(timeStamp1, timeStamp2);

    }

}