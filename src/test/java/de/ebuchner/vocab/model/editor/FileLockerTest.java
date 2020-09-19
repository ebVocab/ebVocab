package de.ebuchner.vocab.model.editor;

import junit.framework.TestCase;

import java.io.File;

public class FileLockerTest extends TestCase {

    public void testNull() {
        FileLocker fileLocker = new FileLocker();
        try {
            fileLocker.isLocked(null);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            fileLocker.lockFile(null);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            fileLocker.unLockFile(null);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    public void testLocker() {
        FileLocker fileLocker = new FileLocker();

        File a1 = new File("a");
        File a2 = new File("a");
        File b = new File("b");

        try {
            fileLocker.unLockFile(a1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            fileLocker.unLockFile(a2);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            fileLocker.unLockFile(b);
            fail();
        } catch (IllegalArgumentException e) {

        }

        assertFalse(fileLocker.isLocked(a1));
        assertFalse(fileLocker.isLocked(a2));
        assertFalse(fileLocker.isLocked(b));

        fileLocker.lockFile(a1);
        assertTrue(fileLocker.isLocked(a1));
        assertTrue(fileLocker.isLocked(a2));
        assertFalse(fileLocker.isLocked(b));

        try {
            fileLocker.lockFile(a1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            fileLocker.lockFile(a2);
            fail();
        } catch (IllegalArgumentException e) {

        }

        fileLocker.lockFile(b);
        assertTrue(fileLocker.isLocked(a1));
        assertTrue(fileLocker.isLocked(a2));
        assertTrue(fileLocker.isLocked(b));

        try {
            fileLocker.lockFile(a1);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            fileLocker.lockFile(a2);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            fileLocker.lockFile(b);
            fail();
        } catch (IllegalArgumentException e) {

        }

        fileLocker.unLockFile(a1);
        assertFalse(fileLocker.isLocked(a1));
        assertFalse(fileLocker.isLocked(a2));
        assertTrue(fileLocker.isLocked(b));

        try {
            fileLocker.lockFile(b);
            fail();
        } catch (IllegalArgumentException e) {

        }

        fileLocker.unLockFile(b);
        assertFalse(fileLocker.isLocked(a1));
        assertFalse(fileLocker.isLocked(a2));
        assertFalse(fileLocker.isLocked(b));
    }
}
