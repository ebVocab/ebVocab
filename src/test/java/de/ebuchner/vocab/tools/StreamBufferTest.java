package de.ebuchner.vocab.tools;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamBufferTest extends TestCase {

    public void testStreamBuffer() {
        try {
            final String buffer = "abcdefghijk";
            MyStream inputStream = new MyStream(buffer.getBytes());
            StreamBuffer streamBuffer = new StreamBuffer(inputStream);
            InputStream duplicateStream = streamBuffer.duplicateInputStream();

            assertEquals(-1, inputStream.read());

            for (int i = 0; i < buffer.getBytes().length; i++) {
                char c = buffer.charAt(i);
                assertEquals(c, duplicateStream.read());
            }

            assertFalse(inputStream.closed);
            duplicateStream.close();
            assertFalse(inputStream.closed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class MyStream extends ByteArrayInputStream {

        boolean closed = false;

        public MyStream(byte[] bytes) {
            super(bytes);
        }

        @Override
        public void close() {
            closed = true;
            try {
                super.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
