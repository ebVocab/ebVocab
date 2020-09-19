package de.ebuchner.vocab.model.practice;

import junit.framework.TestCase;

public class PracticeStateTest extends TestCase {

    class StateHolder implements PracticeStateHolder {
        PracticeState practiceState = new PracticeStateDisabled(this);
        boolean prepareAskNext;

        public void setPracticeState(PracticeState practiceState) {
            this.practiceState = practiceState;
        }

        public PracticeState getPracticeState() {
            return practiceState;
        }

        public void onPrepareAskNext() {
            prepareAskNext = true;
        }
    }

    public void testStates() {
        StateHolder stateHolder = new StateHolder();

        PracticeState practiceState = stateHolder.getPracticeState();

        practiceState.stateDisable();
        assertEquals(PracticeStateDisabled.class, stateHolder.getPracticeState().getClass());

        practiceState.stateNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());
        assertFalse(stateHolder.prepareAskNext);

        practiceState = stateHolder.getPracticeState();
        practiceState.stateDisable();
        assertEquals(PracticeStateDisabled.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());
        assertFalse(stateHolder.prepareAskNext);

        practiceState = stateHolder.getPracticeState();
        practiceState.stateNext();
        assertEquals(PracticeStateShowSolution.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());
        assertTrue(stateHolder.prepareAskNext);

        practiceState = stateHolder.getPracticeState();
        practiceState.stateDisable();
        assertEquals(PracticeStateDisabled.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateAskNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateAskNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateNext();
        assertEquals(PracticeStateShowSolution.class, stateHolder.getPracticeState().getClass());

        practiceState = stateHolder.getPracticeState();
        practiceState.stateAskNext();
        assertEquals(PracticeStateAskNext.class, stateHolder.getPracticeState().getClass());
    }
}
