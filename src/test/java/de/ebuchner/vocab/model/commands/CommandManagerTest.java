package de.ebuchner.vocab.model.commands;

import junit.framework.TestCase;

public class CommandManagerTest extends TestCase {

    int counterA;
    int counterB;

    class CounterACommand implements SimpleCommand {
        public void execute() {
            counterA++;
        }

        public void unExecute() {
            counterA--;
        }
    }

    class CounterBCommand implements SimpleCommand {
        public void execute() {
            counterB++;
        }

        public void unExecute() {
            counterB--;
        }
    }


    @Override
    protected void setUp() {
        counterA = 0;
        counterB = 0;
    }

    public void testUndoRedo() {
        CommandManager<SimpleCommand> ecm = new CommandManager<SimpleCommand>();

        // empty
        assertFalse(ecm.canUndo());
        assertFalse(ecm.isDirty());
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canExecute());

        CounterACommand commandA = new CounterACommand();
        ecm.addCommand(commandA);

        // one command added
        assertFalse(ecm.canUndo());
        assertFalse(ecm.isDirty());
        assertFalse(ecm.canRedo());
        assertTrue(ecm.canExecute());

        // one command executed
        ecm.execute();
        assertEquals(counterA, 1);
        assertTrue(ecm.canUndo());
        assertTrue(ecm.isDirty());
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // undo
        ecm.undo();
        assertEquals(0, counterA);

        assertFalse(ecm.canUndo());
        assertTrue(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // redo
        ecm.redo();
        assertEquals(1, counterA);

        assertTrue(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // clear
        ecm.clear();
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canUndo());
        assertFalse(ecm.canExecute());
    }

    public void testMultipleCommands() {
        CommandManager<SimpleCommand> ecm = new CommandManager<SimpleCommand>();

        CounterACommand commandA = new CounterACommand();
        CounterBCommand commandB = new CounterBCommand();

        // Add commands
        for (int i = 0; i < 5; i++) {
            ecm.addCommand(commandA);
        }
        assertFalse(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertTrue(ecm.canExecute());

        // Execute all
        int expectedCounterA = 5;
        while (ecm.canExecute())
            ecm.execute();
        assertEquals(counterA, expectedCounterA);

        assertTrue(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // Undo some
        for (int i = 0; i < 3; i++) {
            ecm.undo();
            expectedCounterA -= 1;
            assertEquals(counterA, expectedCounterA);

            assertTrue(ecm.canUndo());
            assertTrue(ecm.canRedo());
            assertFalse(ecm.canExecute());
        }

        // Redo one
        ecm.redo();
        expectedCounterA += 1;
        assertEquals(counterA, expectedCounterA);

        assertTrue(ecm.canUndo());
        assertTrue(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // Although still redoable - add another command. Redo buffer is cleared
        ecm.addCommand(commandB);

        assertTrue(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertTrue(ecm.canExecute());

        // Execute next command (should be commandB)
        ecm.execute();
        assertEquals(counterA, expectedCounterA);

        int expectedCounterB = 1;
        assertEquals(counterB, expectedCounterB);

        assertTrue(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // undo commandB and add another commandB to the command list
        ecm.undo();
        assertTrue(ecm.canUndo());
        assertTrue(ecm.canRedo());
        assertFalse(ecm.canExecute());

        expectedCounterB -= 1;
        assertEquals(counterA, expectedCounterA);
        assertEquals(counterB, expectedCounterB);

        ecm.addCommand(commandB);
        assertTrue(ecm.canUndo());
        assertFalse(ecm.canRedo());
        assertTrue(ecm.canExecute());

        // now undo instead of execute -> command ist dropped
        ecm.undo();

        expectedCounterA -= 1;
        assertEquals(counterA, expectedCounterA);
        assertEquals(counterB, expectedCounterB);

        assertTrue(ecm.canUndo());
        assertTrue(ecm.canRedo());
        assertFalse(ecm.canExecute());

        // clear
        ecm.clear();
        assertFalse(ecm.canRedo());
        assertFalse(ecm.canUndo());
        assertFalse(ecm.canExecute());
    }

    public void testUndoRedoSeveralExecutes() {
        CommandManager<SimpleCommand> commandManager = new CommandManager<SimpleCommand>();
        commandManager.addCommand(new CounterACommand());
        commandManager.execute();
        assertEquals(1, counterA);

        commandManager.addCommand(new CounterACommand());
        commandManager.execute();
        assertEquals(2, counterA);

        assertTrue(commandManager.canUndo());
        commandManager.undo();
        assertEquals(1, counterA);

        assertTrue(commandManager.canUndo());
        commandManager.undo();
        assertEquals(0, counterA);

        assertTrue(commandManager.canRedo());
        commandManager.redo();
        assertEquals(1, counterA);

        assertTrue(commandManager.canRedo());
        commandManager.redo();
        assertEquals(2, counterA);
    }

    public void testNoUndoRedo() {
        CommandManager<SimpleCommand> cmd = new CommandManager<SimpleCommand>();
        try {
            assertFalse(cmd.canUndo());
            cmd.undo();
            fail();
        } catch (IllegalStateException e) {

        }
        try {
            assertFalse(cmd.canRedo());
            cmd.redo();
            fail();
        } catch (IllegalStateException e) {

        }
    }
}
