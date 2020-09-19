package de.ebuchner.vocab.tools;

import junit.framework.TestCase;

import java.util.Arrays;

public class FibonacciTest extends TestCase {
    public void testFibonacciNumbers() throws Exception {
        assertEquals(0, new Fibonacci(0).fibonacciNumbers().length);
        assertEquals(1, new Fibonacci(1).fibonacciNumbers().length);
        arrayEquals(
                Arrays.asList(1, 2, 3, 5, 8, 13, 21, 34, 55, 89).toArray(new Integer[10]),
                new Fibonacci(100).fibonacciNumbers()
        );
        arrayEquals(
                Arrays.asList(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144).toArray(new Integer[10]),
                new Fibonacci(150).fibonacciNumbers()
        );
    }

    public void testNextFibonacci() {
        Fibonacci f = new Fibonacci(100);
        assertEquals(0, f.nextFibonacci(0));
        assertEquals(1, f.nextFibonacci(1));
        assertEquals(2, f.nextFibonacci(2));
        assertEquals(3, f.nextFibonacci(3));
        assertEquals(3, f.nextFibonacci(4));
        assertEquals(5, f.nextFibonacci(5));
        assertEquals(5, f.nextFibonacci(6));
        assertEquals(5, f.nextFibonacci(7));
        assertEquals(8, f.nextFibonacci(8));
        assertEquals(55, f.nextFibonacci(88));
        assertEquals(89, f.nextFibonacci(89));
        assertEquals(89, f.nextFibonacci(90));
        assertEquals(89, f.nextFibonacci(Integer.MAX_VALUE));
    }

    private void arrayEquals(Integer[] ref, Integer[] out) {
        assertEquals(ref.length, out.length);
        for (int i = 0; i < ref.length; i++)
            assertEquals(ref[i], out[i]);
    }
}
