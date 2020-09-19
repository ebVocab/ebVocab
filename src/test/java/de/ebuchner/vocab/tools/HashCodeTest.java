package de.ebuchner.vocab.tools;

import de.ebuchner.toolbox.lang.HashCode;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class HashCodeTest extends TestCase {

    List<Integer> previousResults = new ArrayList<Integer>();

    ;

    @Override
    protected void setUp() {
        previousResults.clear();
    }

    private void assertHC(Expected expected, int found) {
        int previous = previousResults.get(previousResults.size() - 1);
        if (expected == Expected.NOT_CHANGED)
            assertEquals(previous, found);
        else if (expected == Expected.CHANGED) {
            assertFalse(previousResults.contains(found));
        }

        previousResults.add(found);
    }

    public void testHashCode() {
        previousResults.add(0);

        HashCode hc = new HashCode(this);

        assertHC(Expected.CHANGED, hc.getResult());

        hc.addBoolean(false);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addBoolean(true);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addBooleanArray(null);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addBooleanArray(new boolean[]{});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addBooleanArray(new boolean[]{true, false});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addBooleanArray(new boolean[]{false, false});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addByte((byte) 0);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addByte((byte) 1);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addByteArray(new byte[]{(byte) 0, (byte) 0});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addByteArray(new byte[]{});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addChar('c');
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addChar('\0');
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addCharArray(new char[]{'e', 'b'});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addDouble(17.1);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addDoubleArray(new double[]{Double.NaN});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addDoubleArray(new double[]{Double.POSITIVE_INFINITY});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addFloat(-42.0f);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addFloatArray(new float[]{1, 2, 3});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addInt(0);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addInt(Integer.MIN_VALUE);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addIntArray(new int[]{-1, 23333, 32222});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addLong(Long.MAX_VALUE - 1);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addLong(0L);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addLongArray(new long[]{0L});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addObject(null);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addObject(new Object());
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addObject("x");
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addObjectArray(new Object[]{null, "null"});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addShort((short) 0);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addShort((short) 123);
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addShortArray(new short[]{});
        assertHC(Expected.CHANGED, hc.getResult());

        hc.addShortArray(null);
        assertHC(Expected.NOT_CHANGED, hc.getResult());

        hc.addShortArray(new short[]{(short) -123});
        assertHC(Expected.CHANGED, hc.getResult());

        for (int x : previousResults) {
            System.out.print(x + " ");
        }
    }

    public void testTwo() {
        MyClass my1 = new MyClass("String 12");
        MyClass my2 = new MyClass("String 12");
        MyClass my3 = new MyClass("String 3");

        assertEquals(my1.hc(), my2.hc());
        assertTrue(my1.hc() != my3.hc());

        HashCode hcObj1 = new HashCode(new Object());
        HashCode hcObj2 = new HashCode(new Object());

        assertEquals(hcObj1.getResult(), hcObj2.getResult());
    }

    enum Expected {NOT_CHANGED, CHANGED}

    class MyClass {
        String string;

        public MyClass(String string) {
            this.string = string;
        }

        public int hc() {
            HashCode hc = new HashCode(this);
            hc.addObject(string);
            return hc.getResult();
        }
    }
}
