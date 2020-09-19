package de.ebuchner.vocab.nui.focus;

import junit.framework.TestCase;

public class AbstractTextFieldWithFocusTest extends TestCase {
    class MyTextField extends AbstractTextFieldWithFocus {
        int caretPosition;
        int selectionStart;
        int selectionEnd;
        String text;
        boolean editable;

        public MyTextField(
                int caretPosition,
                int selectionStart,
                int selectionEnd,
                String text,
                boolean editable
        ) {
            this.caretPosition = caretPosition;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
            this.text = text;
            this.editable = editable;
        }

        public int getCaretPosition() {
            return caretPosition;
        }

        public void setCaretPosition(int caretPosition) {
            this.caretPosition = caretPosition;
        }

        public boolean isEditable() {
            return editable;
        }

        public int getSelectionEnd() {
            return selectionEnd;
        }

        public int getSelectionStart() {
            return selectionStart;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        protected void changeText(String text) {
            this.text = text;
        }
    }

    public void testEmptyField() {
        MyTextField textField = new MyTextField(
                0, 0, 0, "", true
        );

        textField.addText("abc");
        assertEquals("abc", textField.getText());
        assertEquals(3, textField.getCaretPosition());
    }

    public void testFullSelectedField() {
        MyTextField textField = new MyTextField(
                0, 0, 3, "abc", true
        );

        textField.addText("def");
        assertEquals("def", textField.getText());
        assertEquals(3, textField.getCaretPosition());
    }

    public void testBeginSelectedField() {
        MyTextField textField = new MyTextField(
                0, 0, 2, "abc", true
        );

        textField.addText("xy");
        assertEquals("xyc", textField.getText());
        assertEquals(2, textField.getCaretPosition());
    }

    public void testEndSelectedField() {
        MyTextField textField = new MyTextField(
                0, 2, 3, "abc", true
        );

        textField.addText("xy");
        assertEquals("abxy", textField.getText());
        assertEquals(4, textField.getCaretPosition());
    }

    public void testMiddleSelectedField() {
        MyTextField textField = new MyTextField(
                0, 5, 8, "what the heck", true
        );

        textField.addText("a");
        assertEquals("what a heck", textField.getText());
        assertEquals(6, textField.getCaretPosition());
    }


    public void testNoFieldCaretNot0() {
        MyTextField textField = new MyTextField(
                9, 0, 0, "what the heck", true
        );

        textField.addText("damn ");
        assertEquals("what the damn heck", textField.getText());
        assertEquals(14, textField.getCaretPosition());
    }

    public void testBackspaceEmpty() {
        MyTextField textField = new MyTextField(
                0, 0, 0, "", true
        );
        textField.onKeyBackspace();
        assertEquals("", textField.getText());
        assertEquals(0, textField.getCaretPosition());
    }

    public void testBackspaceNotSelected() {
        MyTextField textField = new MyTextField(
                4, 0, 0, "hello world", true
        );
        textField.onKeyBackspace();
        assertEquals("helo world", textField.getText());
        assertEquals(3, textField.getCaretPosition());
    }

    public void testBackspaceNotSelectedCaretAtEnd() {
        MyTextField textField = new MyTextField(
                11, 0, 0, "hello world", true
        );
        textField.onKeyBackspace();
        assertEquals("hello worl", textField.getText());
        assertEquals(10, textField.getCaretPosition());
    }

    public void testNotEditable() {
        MyTextField textField = new MyTextField(
                4, 0, 0, "hello world", false
        );
        textField.addText("abc");
        assertEquals("hello world", textField.getText());
        assertEquals(4, textField.getCaretPosition());

        textField.onKeyBackspace();
        assertEquals("hello world", textField.getText());
        assertEquals(4, textField.getCaretPosition());
    }
}
