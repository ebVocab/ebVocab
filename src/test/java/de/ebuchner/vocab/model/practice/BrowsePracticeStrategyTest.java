package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class BrowsePracticeStrategyTest extends TestCase {

    public void testEmpty() {
        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
        assertEquals(0, strategy.getPracticeSize());
        strategy.clear();
        strategy.reShuffle();
        assertNull(strategy.currentRef());
        strategy.gotoNextRef();
        assertNull(strategy.currentRef());
        strategy.gotoPreviousRef();
        assertNull(strategy.currentRef());
    }

    public void testSingleEntry() {
        String id = "id";
        VocabEntryRef ref = new VocabEntryRef(null, id);

        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
        strategy.addModelRef(ref);
        assertEquals(1, strategy.getPracticeSize());
        strategy.reShuffle();
        assertEquals(1, strategy.getPracticeSize());

        assertEquals(ref, strategy.currentRef());
        strategy.gotoNextRef();
        assertEquals(ref, strategy.currentRef());
        strategy.gotoPreviousRef();
        assertEquals(ref, strategy.currentRef());

        strategy.removeModelRef(ref);
        assertEquals(0, strategy.getPracticeSize());
        strategy.reShuffle();
        assertEquals(0, strategy.getPracticeSize());
        assertNull(strategy.currentRef());
        strategy.gotoNextRef();
        assertNull(strategy.currentRef());
        strategy.gotoPreviousRef();
        assertNull(strategy.currentRef());

        strategy.addModelRef(ref);
        strategy.reShuffle();
        assertEquals(1, strategy.getPracticeSize());

        strategy.clear();
        assertEquals(0, strategy.getPracticeSize());
    }

    public void testRefCheck() {
        VocabEntryRef ref1 = new VocabEntryRef(null, "id1");
        VocabEntryRef refX = new VocabEntryRef(null, "idX");
        VocabEntryRef ref2 = new VocabEntryRef(null, "id2");
        VocabEntryRef ref2Duplicate = new VocabEntryRef(null, ref2.getId());
        VocabEntryRef ref3 = new VocabEntryRef(null, "id3");

        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
        strategy.addModelRef(ref1);
        assertFalse(strategy.removeModelRef(refX));

        strategy.addModelRef(ref2);
        strategy.reShuffle();
        assertEquals(2, strategy.getPracticeSize());
        strategy.addModelRef(ref2Duplicate);
        assertEquals(3, strategy.getPracticeSize());

        strategy.addModelRef(ref3);
        strategy.reShuffle();
        assertEquals(4, strategy.getPracticeSize());

        strategy.reShuffle();
        assertEquals(ref1, strategy.currentRef());

        strategy.gotoNextRef();
        assertEquals(ref2, strategy.currentRef());
        assertEquals(ref2, strategy.currentRef());

        strategy.gotoNextRef();
        assertEquals(ref2Duplicate, strategy.currentRef());
        assertEquals(ref2Duplicate, strategy.currentRef());

        strategy.gotoNextRef();
        assertEquals(ref3, strategy.currentRef());
        assertEquals(ref3, strategy.currentRef());

        strategy.gotoNextRef();
        assertEquals(ref1, strategy.currentRef());
        assertEquals(ref1, strategy.currentRef());

        strategy.gotoPreviousRef();
        assertEquals(ref3, strategy.currentRef());
        assertEquals(ref3, strategy.currentRef());

        strategy.gotoPreviousRef();
        assertEquals(ref2Duplicate, strategy.currentRef());
        assertEquals(ref2Duplicate, strategy.currentRef());

        strategy.gotoPreviousRef();
        assertEquals(ref2, strategy.currentRef());
        assertEquals(ref2, strategy.currentRef());

        strategy.gotoPreviousRef();
        assertEquals(ref1, strategy.currentRef());
        assertEquals(ref1, strategy.currentRef());

        strategy.removeModelRef(ref2);
        assertEquals(2, strategy.getPracticeSize());
    }

    public void testArrayOperations() {
        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();

        List<VocabEntryRef> refList0to9 = new ArrayList<VocabEntryRef>();
        for (int i = 0; i < 10; i++) {
            refList0to9.add(new VocabEntryRef(null, "id" + i));
        }

        List<VocabEntryRef> refList10to19 = new ArrayList<VocabEntryRef>();
        List<VocabEntryRef> refList14to16 = new ArrayList<VocabEntryRef>();

        for (int i = 10; i < 20; i++) {
            VocabEntryRef ref = new VocabEntryRef(null, "id" + i);
            refList10to19.add(ref);
            if (i >= 14 && i <= 16)
                refList14to16.add(ref);
        }

        addRefList(strategy, refList0to9);
        addRefList(strategy, refList10to19);
        strategy.reShuffle();

        assertEquals(20, strategy.getPracticeSize());
        removeRefList(strategy, refList14to16);
        strategy.reShuffle();
        assertEquals(17, strategy.getPracticeSize());
    }

    public void testRemove() {
        final int size = 10;
        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();

        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        for (int i = 0; i < size; i++) {
            refList.add(new VocabEntryRef(null, "id" + i));
        }

        addRefList(strategy, refList);
        strategy.reShuffle();
        VocabEntryRef myRef = null;
        for (int i = 0; i < size / 2; i++) {
            strategy.gotoNextRef();
            assertNotNull(myRef = strategy.currentRef());
        }

        while (refList.size() > 1) {

            {
                VocabEntryRef currentRef = strategy.currentRef();
                VocabEntryRef ref2Delete = refList.get(refList.size() - 1);
                refList.remove(ref2Delete);
                strategy.removeModelRef(ref2Delete);
                VocabEntryRef newRef = strategy.currentRef();
                System.out.println("Before delete of " + ref2Delete.getId() + " Was: " + currentRef.getId() + " Is: " + newRef.getId());
                if (idOf(ref2Delete) > idOf(currentRef))
                    assertSame(currentRef, newRef);
                else
                    break;
            }
            {
                VocabEntryRef currentRef = strategy.currentRef();
                VocabEntryRef ref2Delete = refList.get(0);
                refList.remove(ref2Delete);
                strategy.removeModelRef(ref2Delete);
                VocabEntryRef newRef = strategy.currentRef();
                System.out.println("Before delete of " + ref2Delete.getId() + " Was: " + currentRef.getId() + " Is: " + newRef.getId());
                if (idOf(ref2Delete) < idOf(currentRef))
                    assertSame(idOf(currentRef) + 1, idOf(newRef));
                else
                    break;
            }
        }
    }

    private int idOf(VocabEntryRef currentRef) {
        return Integer.parseInt(currentRef.getId().substring(2, 3));
    }

    public void testPositionForward() {
        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        for (int i = 0; i < 3; i++) {
            refList.add(new VocabEntryRef(null, "id" + i));
        }
        BrowsePracticeStrategy strategy = createStrategy(refList);

        assertEquals(strategy.currentRef().getId(), "id0");

        for (int i = 1; i < 20; i++) {
            strategy.gotoNextRef();
            assertNotNull(strategy.currentRef());
            assertNotNull(strategy.currentRef());
            assertEquals("id" + (i % 3), strategy.currentRef().getId());
        }

    }

    private BrowsePracticeStrategy createStrategy(List<VocabEntryRef> refList) {
        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
        addRefList(strategy, refList);
        strategy.reShuffle();
        return strategy;
    }

    public void testPositionBackward() {
        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        for (int i = 0; i < 3; i++) {
            refList.add(new VocabEntryRef(null, "id" + i));
        }
        BrowsePracticeStrategy strategy = createStrategy(refList);

        assertEquals(strategy.currentRef().getId(), "id0");

        for (int i = 23; i > 0; i--) {
            int id = i % 3;
            strategy.gotoPreviousRef();
            assertNotNull(strategy.currentRef());
            assertNotNull(strategy.currentRef());
            assertEquals("test position " + i, "id" + id, strategy.currentRef().getId());
        }

    }

    public void testClear() {
        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        for (int i = 0; i < 3; i++) {
            refList.add(new VocabEntryRef(null, "id" + i));
        }
        BrowsePracticeStrategy strategy = createStrategy(refList);
        strategy.clear();
        assertNull(strategy.currentRef());
        strategy.gotoNextRef();
        assertNull(strategy.currentRef());
        strategy.gotoPreviousRef();
        assertNull(strategy.currentRef());
    }

    private void removeRefList(AbstractPracticeStrategy strategy, List<VocabEntryRef> refList) {
        for (VocabEntryRef ref : refList)
            strategy.removeModelRef(ref);
    }

    private void addRefList(AbstractPracticeStrategy strategy, List<VocabEntryRef> refList) {
        for (VocabEntryRef ref : refList)
            strategy.addModelRef(ref);
    }
}
