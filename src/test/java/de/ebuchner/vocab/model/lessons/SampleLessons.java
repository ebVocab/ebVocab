package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.config.ConfigConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SampleLessons {

    private File root;

    public SampleLessons() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        assertTrue(tempDir.exists());

        this.root = createDirectory(tempDir, getClass().getName() + ".vocab");
        assertTrue(root.exists());
        createVocabFile(root, "root-1");

        File topicA = createDirectory(root, "topicA");
        createVocabFile(topicA, "a-1");
        createVocabFile(topicA, "a-2");
        createVocabFile(topicA, "a-3");
        createOtherFile(topicA, "a-4", "txt");

        File topicB = createDirectory(root, "topicB");
        createVocabFile(topicB, "b-1");
        createVocabFile(topicB, "b-2");

        File topicBSub = createDirectory(topicB, "topicBSub");
        createVocabFile(topicBSub, "b-sub-1");
        createVocabFile(topicBSub, "b-sub-2");

        createDirectory(root, "topicC");
    }

    protected void tearDown() {
        assertTrue(root.exists());
        emptyDir(root);
        assertFalse(root.exists());
    }

    private void emptyDir(File dir) {
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isFile())
                assertTrue(file.delete());
            else
                emptyDir(file);
        }
        assertTrue(dir.delete());
    }

    private File createDirectory(File baseDir, String name) {
        File subDir = new File(baseDir, name);
        if (subDir.exists())
            emptyDir(subDir);
        assertTrue(subDir.mkdir());

        return subDir;
    }

    private File createOtherFile(File baseDir, String name, String ext) {
        File file = new File(baseDir, name + "." + ext);
        PrintWriter p;
        try {
            p = new PrintWriter(new FileWriter(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            p.println("This is the content of " + name);
        } finally {
            p.close();
        }
        return file;
    }

    private File createVocabFile(File baseDir, String name) {
        return createOtherFile(baseDir, name, ConfigConstants.FILE_EXTENSION);
    }

    private void assertTrue(boolean condition) {
        if (!condition)
            throw new RuntimeException("Expected another result");
    }

    private void assertFalse(boolean condition) {
        if (condition)
            throw new RuntimeException("Expected another result");
    }

    public File getRoot() {
        return root;
    }
}
