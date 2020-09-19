package de.ebuchner.vocab.model.commands;

import junit.framework.TestCase;

public class CompositeCommandTest extends TestCase {

    int counter;

    class CounterCommand implements SimpleCommand {

        public void execute() {
            counter++;
        }

        public void unExecute() {
            counter--;
        }
    }

    public void testCommands() {
        CounterCommand counterCmd = new CounterCommand();
        CompositeCommand aggregate = new CompositeCommand();

        for (int i = 0; i < 5; i++) {
            aggregate.getCommands().add(counterCmd);
        }

        counter = 0;

        aggregate.execute();

        assertEquals(5, counter);

        aggregate.unExecute();

        assertEquals(0, counter);
    }

}
