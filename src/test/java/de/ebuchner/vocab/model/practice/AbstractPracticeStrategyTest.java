package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.model.core.ModelChangeEvent;
import de.ebuchner.vocab.model.core.ModelCommandManagerClearedEvent;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import de.ebuchner.vocab.nui.NuiStarter;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class AbstractPracticeStrategyTest extends TestCase {

    @Override
    protected void setUp() {
        new NuiStarter().prepareProjectDir();
    }

    @Override
    protected void tearDown() throws Exception {
        Config.instance().dispose();
    }

    public void testCTor() {
        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        refList.add(new VocabEntryRef(null, "id1"));
        refList.add(new VocabEntryRef(null, "id2"));
        refList.add(new VocabEntryRef(null, "id3"));

        {
            BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
            addRefList(strategy, refList);
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.reShuffle();
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.clear();
            assertEquals(0, strategy.getPracticeSize());
        }

        {
            BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
            addRefList(strategy, refList);
            strategy.reShuffle();
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.clear();
            assertEquals(0, strategy.getPracticeSize());
        }

        {
            RandomPracticeStrategy strategy = new RandomPracticeStrategy();
            addRefList(strategy, refList);
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.reShuffle();
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.clear();
            assertEquals(0, strategy.getPracticeSize());
        }

        {
            RandomPracticeStrategy strategy = new RandomPracticeStrategy();
            addRefList(strategy, refList);
            strategy.reShuffle();
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.clear();
            assertEquals(0, strategy.getPracticeSize());
        }

        final int groupSize = 3;
        final int repetitionCount = 5;
        {
            IntensePracticeStrategy strategy = new IntensePracticeStrategy(
                    groupSize, repetitionCount);
            addRefList(strategy, refList);
            assertEquals(refList.size(), strategy.getPracticeSize());
            strategy.clear();
            assertEquals(0, strategy.getPracticeSize());
        }
    }

    public void testCommands() {

        List<VocabEntryRef> refList = new ArrayList<VocabEntryRef>();
        refList.add(new VocabEntryRef(null, "id1"));
        refList.add(new VocabEntryRef(null, "id2"));
        refList.add(new VocabEntryRef(null, "id3"));
        refList.add(new VocabEntryRef(null, "id4"));
        BrowsePracticeStrategy strategy = new BrowsePracticeStrategy();
        addRefList(strategy, refList);
        strategy.reShuffle();

        TestListener listener = new TestListener();
        strategy.addListener(listener);

        strategy.executeCommand(new StrategyChange(strategy));
        strategy.executeCommand(new StrategyChange(strategy));

        assertEquals(2, listener.changeEvents);
        assertEquals(0, listener.clearEvents);
        assertTrue(strategy.canUndo());
        assertFalse(strategy.canRedo());

        assertEquals("id3", strategy.currentRef().getId());

        strategy.undo();
        assertEquals("id2", strategy.currentRef().getId());

        strategy.undo();
        assertEquals("id1", strategy.currentRef().getId());

        assertEquals(4, listener.changeEvents);
        assertEquals(0, listener.clearEvents);

        strategy.removeListener(listener);
        strategy.executeCommand(new StrategyChange(strategy));
        assertEquals("id2", strategy.currentRef().getId());
        assertEquals(4, listener.changeEvents);

        assertTrue(strategy.canUndo());
        strategy.undo();
        assertEquals("id1", strategy.currentRef().getId());

        assertEquals(0, listener.clearEvents);
        strategy.addListener(listener);
        strategy.reShuffle();
        assertEquals(1, listener.clearEvents);
    }

    private void addRefList(AbstractPracticeStrategy strategy, List<VocabEntryRef> refList) {
        for (VocabEntryRef ref : refList)
            strategy.addModelRef(ref);
    }

    class TestListener implements StrategyChangeListener {
        int changeEvents = 0;
        int clearEvents = 0;

        public void modelCommandManagerCleared(ModelCommandManagerClearedEvent event) {
            clearEvents++;
        }

        public void strategyChanged(ModelChangeEvent event) {
            changeEvents++;
        }
    }

}
