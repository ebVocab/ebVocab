package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.util.*;

public class IntensePracticeStrategyTest extends TestCase {

    public void testAddRemoveReferences() {
        IntensePracticeStrategy strategy = new IntensePracticeStrategy();
        strategy.gotoNextRef();
        assertNull(strategy.currentRef());
        assertNull(strategy.currentRef());
        strategy.gotoPreviousRef();
        assertNull(strategy.currentRef());
        assertEquals(0, strategy.getPracticeSize());
        strategy.reShuffle();
        assertEquals(0, strategy.getPracticeSize());
        strategy.gotoNextRef();
        assertNull(strategy.currentRef());
        assertNull(strategy.currentRef());
        strategy.gotoPreviousRef();
        assertNull(strategy.currentRef());

        VocabEntryRef ref0 = new VocabEntryRef(null, "id0");
        strategy.addModelRef(ref0);
        assertSequence(
                strategy,
                Arrays.asList("id0", "id0", "id0")
        );

        VocabEntryRef ref1 = new VocabEntryRef(null, "id1");
        strategy.addModelRef(ref1);
        assertSequence(
                strategy,
                Arrays.asList("id0", "id1", "id0", "id1", "id0", "id1")
        );

        VocabEntryRef ref2 = new VocabEntryRef(null, "id2");
        strategy.addModelRef(ref2);
        assertSequence(
                strategy,
                Arrays.asList("id0", "id1", "id2", "id0", "id1", "id2", "id0", "id1", "id2")
        );

        VocabEntryRef ref3 = new VocabEntryRef(null, "id3");
        strategy.addModelRef(ref3);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id3", "id3", "id3"
                )
        );

        VocabEntryRef ref4 = new VocabEntryRef(null, "id4");
        strategy.addModelRef(ref4);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id3", "id4",
                        "id3", "id4",
                        "id3", "id4"
                )
        );

        VocabEntryRef ref5 = new VocabEntryRef(null, "id5");
        strategy.addModelRef(ref5);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id3", "id4", "id5",
                        "id3", "id4", "id5",
                        "id3", "id4", "id5"
                )
        );

        VocabEntryRef ref6 = new VocabEntryRef(null, "id6");
        strategy.addModelRef(ref6);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id0", "id1", "id2",
                        "id3", "id4", "id5", "id6",
                        "id3", "id4", "id5", "id6",
                        "id3", "id4", "id5", "id6"
                )
        );

        assertEquals(0, strategy.getPracticePosition());
        assertEquals("id0", strategy.currentRef().getId());

        strategy.reShuffle();
        assertEquals(3, strategy.getPracticePosition());
        assertEquals("id3", strategy.currentRef().getId());

        strategy.reShuffle();
        assertEquals(0, strategy.getPracticePosition());
        assertEquals("id0", strategy.currentRef().getId());

        strategy.reShuffle();
        assertEquals(3, strategy.getPracticePosition());
        assertEquals("id3", strategy.currentRef().getId());

        strategy.gotoNextRef();
        strategy.gotoNextRef();
        assertEquals(5, strategy.getPracticePosition());
        strategy.reShuffle();
        assertEquals(0, strategy.getPracticePosition());
        assertEquals("id0", strategy.currentRef().getId());

        strategy.gotoNextRef();
        strategy.gotoNextRef();
        strategy.gotoNextRef();
        strategy.gotoNextRef();
        strategy.gotoNextRef();
        strategy.gotoNextRef();
        assertEquals(0, strategy.getPracticePosition());
        strategy.reShuffle();
        assertEquals(3, strategy.getPracticePosition());
        assertEquals("id3", strategy.currentRef().getId());

        strategy.gotoNextRef();
        strategy.reShuffle();
        assertEquals(0, strategy.getPracticePosition());
        assertEquals("id0", strategy.currentRef().getId());

        strategy.removeModelRef(ref0);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id1", "id2",
                        "id1", "id2",
                        "id1", "id2",
                        "id3", "id4", "id5", "id6",
                        "id3", "id4", "id5", "id6",
                        "id3", "id4", "id5", "id6"
                )
        );

        strategy.removeModelRef(ref4);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id1", "id2",
                        "id1", "id2",
                        "id1", "id2",
                        "id3", "id5", "id6",
                        "id3", "id5", "id6",
                        "id3", "id5", "id6"
                )
        );

        strategy.removeModelRef(ref6);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id1", "id2",
                        "id1", "id2",
                        "id1", "id2",
                        "id3", "id5",
                        "id3", "id5",
                        "id3", "id5"
                )
        );

        strategy.gotoNextRef();
        strategy.gotoNextRef();

        strategy.removeModelRef(ref1);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id2",
                        "id2",
                        "id2",
                        "id3", "id5",
                        "id3", "id5",
                        "id3", "id5"
                )
        );

        strategy.gotoNextRef();
        strategy.gotoNextRef();

        strategy.removeModelRef(ref3);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id2",
                        "id2",
                        "id2",
                        "id5",
                        "id5",
                        "id5"
                )
        );

        strategy.gotoNextRef();
        strategy.gotoNextRef();

        strategy.removeModelRef(ref2);
        assertSequence(
                strategy,
                Arrays.asList(
                        "id5",
                        "id5",
                        "id5"
                )
        );

        strategy.gotoNextRef();
        strategy.gotoNextRef();

        strategy.removeModelRef(ref5);
        assertSequence(
                strategy,
                Collections.<String>emptyList()
        );
    }

    private void assertSequence(IntensePracticeStrategy strategy, List<String> ids) {
        System.out.println();
        if (strategy.getPracticeSize() > 0)
            strategy.setPosition(0);
        Set<String> uniqueIDs = new HashSet<String>();
        uniqueIDs.addAll(ids);
        //assertEquals(uniqueIDs.size(), strategy.getPracticeSize());
        for (String id : ids) {
            System.out.println("Expecting " + id);
            assertEquals(id, strategy.currentRef().getId());
            strategy.gotoNextRef();
        }
    }
}
