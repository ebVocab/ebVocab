package de.ebuchner.vocab.tools;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FibonacciMapTest extends TestCase {

    private static final int TIMES_0 = 39;
    private static final int TIMES_1 = 66;
    private static final int TIMES_90 = 10;
    private static final int TIMES_5 = 15;
    private static final int TIMES_9 = 25;
    private static final int TIMES_2 = 34;
    private static final int TIMES_6 = 44;

    FibonacciMap<FibonacciMap.IntValueHaving> fibonacciMap = new FibonacciMap<FibonacciMap.IntValueHaving>();

    public void testEmptyMap() {
        assertEquals(0, fibonacciMap.mapFrom(new ArrayList<FibonacciMap.IntValueHaving>()).size());
        assertEquals(0, fibonacciMap.mapFrom(null).size());
    }

    public void testMap() {
        List<FibonacciMap.IntValueHaving> testList = new ArrayList<FibonacciMap.IntValueHaving>();
        for (int i = 0; i < TIMES_0; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 0;
                }
            });
        for (int i = 0; i < TIMES_1; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 1;
                }
            });
        for (int i = 0; i < TIMES_2; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 2;
                }
            });
        for (int i = 0; i < TIMES_5; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 5;
                }
            });
        for (int i = 0; i < TIMES_6; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 6;
                }
            });
        for (int i = 0; i < TIMES_9; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 9;
                }
            });
        for (int i = 0; i < TIMES_90; i++)
            testList.add(new FibonacciMap.IntValueHaving() {
                public int value() {
                    return 90;
                }
            });

        Map<Integer, List<FibonacciMap.IntValueHaving>> result = fibonacciMap.mapFrom(testList);

        assertEquals(6, result.size());

        assertEquals(TIMES_0, result.get(0).size());
        for (FibonacciMap.IntValueHaving val : result.get(0)) {
            assertEquals(0, val.value());
        }

        assertEquals(TIMES_1, result.get(1).size());
        for (FibonacciMap.IntValueHaving val : result.get(1)) {
            assertEquals(1, val.value());
        }

        assertEquals(TIMES_2, result.get(2).size());
        for (FibonacciMap.IntValueHaving val : result.get(2)) {
            assertEquals(2, val.value());
        }

        assertEquals(TIMES_5 + TIMES_6, result.get(5).size());
        for (FibonacciMap.IntValueHaving val : result.get(5)) {
            assertTrue(val.value() >= 5 && val.value() <= 6);
        }

        assertEquals(TIMES_9, result.get(8).size());
        for (FibonacciMap.IntValueHaving val : result.get(8)) {
            assertEquals(9, val.value());
        }

        assertEquals(TIMES_90, result.get(89).size());
        for (FibonacciMap.IntValueHaving val : result.get(89)) {
            assertEquals(90, val.value());
        }

    }
}
