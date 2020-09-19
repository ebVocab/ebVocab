package de.ebuchner.vocab.tools;

import de.ebuchner.toolbox.lang.Equals;
import de.ebuchner.toolbox.lang.HashCode;
import junit.framework.TestCase;

public class EqualsTest extends TestCase {
    public void testEquals() {
        Parent parent = new Parent();
        Parent otherParent = new Parent();

        Child child = new Child();
        Child otherChild = new Child();

        GrandChild grandChild = new GrandChild();
        GrandChild otherGrandChild = new GrandChild();

        String string = "String";
        Object nullObject = null;


        assertTrue(parent.testEquals(otherParent));
        assertFalse(parent.testEquals(otherChild));
        assertFalse(parent.testEquals(otherGrandChild));
        assertFalse(parent.testEquals(string));
        assertFalse(parent.testEquals(nullObject));

        assertFalse(child.testEquals(otherParent));
        assertTrue(child.testEquals(otherChild));
        assertFalse(child.testEquals(otherGrandChild));
        assertFalse(child.testEquals(string));
        assertFalse(child.testEquals(nullObject));

        assertFalse(grandChild.testEquals(otherParent));
        assertFalse(grandChild.testEquals(otherChild));
        assertTrue(grandChild.testEquals(otherGrandChild));
        assertFalse(grandChild.testEquals(string));
        assertFalse(grandChild.testEquals(nullObject));
    }

    class Parent {
        @Override
        public int hashCode() {
            HashCode hashCode = new HashCode(this);
            return hashCode.getResult();
        }

        public boolean testEquals(Object o) {
            return new Equals(this).compareWith(o);
        }
    }

    class Child extends Parent {
    }

    class GrandChild extends Child {
    }
}
