package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class RandomPracticeStrategyTest extends TestCase {

    public void testRandom() {
        final int size = 50;
        RandomPracticeStrategy strategy = new RandomPracticeStrategy();

        for (int i = 0; i < size; i++) {
            strategy.addModelRef(new VocabEntryRef(null, "id" + i));
        }

        for (int repetition = 0; repetition < 200; repetition++) {
            strategy.reShuffle();
            assertEquals(size, strategy.getPracticeSize());
            Set<String> idSet = new HashSet<String>();

            for (int i = 0; i < size; i++) {
                VocabEntryRef ref;
                ref = strategy.currentRef();
                assertFalse("repetition=" + repetition + " i=" + i, idSet.contains(ref.getId()));
                idSet.add(ref.getId());
                strategy.gotoNextRef();
            }

            assertEquals(strategy.getPracticeSize(), idSet.size());
        }
    }

    public void testNoTwoSameVocabsAfterReshuffle() {
        RandomPracticeStrategy strategy = new RandomPracticeStrategy();

        final int size = 3;
        for (int i = 0; i < size; i++) {
            strategy.addModelRef(new VocabEntryRef(null, "id" + i));
        }

        VocabEntryRef previousRef = null;
        for (int loop = 0; loop < 50; loop++) {
            strategy.gotoNextRef();
            VocabEntryRef ref = strategy.currentRef();
            if (previousRef != null) {
                assertFalse(
                        "vocab at iteration " + loop + " contains same id as previous vocab",
                        previousRef.getId().equals(ref.getId())
                );
            }
            previousRef = ref;
        }

    }

}
