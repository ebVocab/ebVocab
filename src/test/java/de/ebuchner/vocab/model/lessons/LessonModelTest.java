package de.ebuchner.vocab.model.lessons;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.nui.NuiStarter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LessonModelTest extends LessonTestBase {

    private SampleLessons sampleLessons;

    @Override
    protected void setUp() {
        sampleLessons = new SampleLessons();
    }

    public void testModelTraversal() {
        LessonModel model = new LessonModel(sampleLessons.getRoot());

        LessonContainer rootContainer = model.getRoot();
        assertNotNull(rootContainer);
        assertEquals(rootContainer.getName(), sampleLessons.getRoot().getName());
        assertEquals(rootContainer.getFileRef(), sampleLessons.getRoot());
        assertTrue(rootContainer.isRoot());
        assertEquals(LessonState.UN_SELECTED, rootContainer.getState());
        assertEquals(0, model.countSelectedLessons());

        assertEquals(rootContainer.size(), 4);
        List<String> names = new ArrayList<String>();
        names.addAll(Arrays.asList("root-1", "topicA", "topicB", "topicC"));

        for (Lesson lesson : rootContainer.lessons()) {
            assertEquals(LessonState.UN_SELECTED, lesson.getState());
            String name = lesson.getName();
            assertTrue("not found: " + name, names.remove(name));
            if (!name.equals("root-1"))
                assertTrue(lesson instanceof LessonContainer);
            if (name.equals("topicA"))
                assertTrue(((LessonContainer) lesson).size() == 3);
            if (name.equals("topicB")) {
                LessonContainer topicB = (LessonContainer) lesson;
                assertTrue(topicB.size() == 3);
                LessonContainer topicBSub = null;
                for (Lesson lessonSub : topicB.lessons()) {
                    assertEquals(LessonState.UN_SELECTED, lessonSub.getState());
                    if (lessonSub instanceof LessonContainer) {
                        assertNull(topicBSub);
                        topicBSub = (LessonContainer) lessonSub;

                        assertEquals(topicBSub.size(), 2);
                    }
                }
            }
            if (name.equals("topicC"))
                assertTrue(((LessonContainer) lesson).size() == 0);
        }
        assertTrue(names.isEmpty());
    }

    public void testIllegalRoot() {
        try {
            new LessonModel(new File("does-not-exist"));
            fail("should fail");
        } catch (IllegalArgumentException iae) {

        }
    }

    @Override
    protected void tearDown() {
        sampleLessons.tearDown();
        Config.instance().dispose();
    }

    public void testPreferences() {
        LessonModel modelOut = new LessonModel(sampleLessons.getRoot());

        List<String> allSelectedNames = Arrays.asList("topicA", "b-1", "b-sub-1", "b-sub-2");
        List<String> lessonNames = Arrays.asList("a-1", "a-2", "a-3", "b-1", "b-sub-1", "b-sub-2");
        for (String name : allSelectedNames) {
            modelOut.executeCommand(new LessonSelectionChange(
                    findLesson(modelOut, name), LessonState.SELECTED
            ));
        }

        new NuiStarter().prepareProjectDir();
        modelOut.saveToPreferences(Config.instance().preferences().getPreferenceValueList());

        LessonModel modelIn = new LessonModel(new SampleLessons().getRoot());
        assertAllChildren(modelIn.getRoot(), LessonState.UN_SELECTED);

        modelIn.restoreFromPreferences(Config.instance().preferences().getPreferenceValueList());
        compareState(modelOut.getRoot(), modelIn.getRoot());

        List<Lesson> selectedLessons = modelOut.getSelectedLessons();

        assertEquals(lessonNames.size(), modelOut.countSelectedLessons());
        assertEquals(lessonNames.size(), selectedLessons.size());
        for (String name : lessonNames) {
            boolean found = false;
            for (Lesson lesson : selectedLessons) {
                if (lesson.getName().equals(name))
                    found = true;
            }
            assertTrue("Missing lesson " + name, found);
        }
    }

    private void compareState(Lesson nodeOut, Lesson nodeIn) {
        assertEquals(nodeOut.getState(), nodeIn.getState());
        if (nodeOut.isContainer()) {
            int posOut = 0;
            for (Lesson childOut : nodeOut.asContainer().lessons()) {
                int posIn = 0;
                for (Lesson childIn : nodeIn.asContainer().lessons()) {
                    if (posIn == posOut) {
                        compareState(childOut, childIn);
                        break;
                    }
                    posIn++;
                }
                posOut++;
            }
        }
    }

    public void testReSynchronizeRemoveRoot() {
        SampleModel sampleModel = new SampleModel();
        try {
            List<File> fileReferenceList = sampleModel.getAllFiles();

            // sanity duplicate
            assertTrue(fileReferenceList.size() > 0);
            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // remove root
            sampleModel.removeDirectory(sampleModel.root);
            fileReferenceList.clear();
            fileReferenceList.add(sampleModel.root);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

        } finally {
            sampleModel.removeSampleModel();
        }
    }

    public void testReSynchronize() {
        SampleModel sampleModel = new SampleModel();
        try {
            List<File> fileReferenceList = sampleModel.getAllFiles();
            sampleModel.assertFilesRef(fileReferenceList);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // remote root vocab file
            assertTrue(sampleModel.root_vocab1.delete());
            assertTrue(fileReferenceList.remove(sampleModel.root_vocab1));
            assertFalse(sampleModel.root_vocab1.exists());

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // add new file
            File root_vocab3 = new File(sampleModel.root, "root_vocab3." + ConfigConstants.FILE_EXTENSION);
            sampleModel.makeFile(root_vocab3);
            fileReferenceList.add(root_vocab3);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // add new directory with three files
            File root_dir4 = new File(sampleModel.root, "root_dir4");
            root_dir4.mkdirs();
            fileReferenceList.add(root_dir4);

            for (int i = 1; i <= 3; i++) {
                File dir4_vocab = new File(root_dir4, "dir4_vocab" + i + "." + ConfigConstants.FILE_EXTENSION);
                sampleModel.makeFile(dir4_vocab);
                fileReferenceList.add(dir4_vocab);
            }

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // remove empty directory
            sampleModel.removeDirectory(sampleModel.root_dir3Empty);
            fileReferenceList.remove(sampleModel.root_dir3Empty);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // remove directory with one file
            sampleModel.removeDirectory(sampleModel.root_dir2);
            fileReferenceList.remove(sampleModel.root_dir2);
            fileReferenceList.remove(sampleModel.dir2_vocab21);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

            // remove directory with sub directories
            sampleModel.removeDirectory(sampleModel.root_dir1);
            fileReferenceList.remove(sampleModel.root_dir1);
            fileReferenceList.remove(sampleModel.dir1_dir11);
            fileReferenceList.remove(sampleModel.dir1_vocab11);
            fileReferenceList.remove(sampleModel.dir1_vocab12);
            fileReferenceList.remove(sampleModel.dir11_vocab111);

            sampleModel.getLessonModel().reSynchronize();
            sampleModel.assertFilesRef(fileReferenceList);

        } finally {
            sampleModel.removeSampleModel();
        }
    }

    static class SampleModel {
        LessonModel lessonModel;

        File root = new File(
                System.getProperty("java.io.tmpdir"),
                LessonModelTest.class.getName() + "." + System.currentTimeMillis()
        );
        File root_dir1 = new File(root, "root_dir1");
        File root_dir2 = new File(root, "root_dir2");

        File dir2_vocab21 = new File(root_dir2, "dir2_vocab21." + ConfigConstants.FILE_EXTENSION);

        File root_dir3Empty = new File(root, "root_dir3Empty");

        File root_vocab1 = new File(root, "root_vocab1." + ConfigConstants.FILE_EXTENSION);
        File root_vocab2 = new File(root, "root_vocab2." + ConfigConstants.FILE_EXTENSION);

        File dir1_vocab11 = new File(root_dir1, "dir1_vocab11." + ConfigConstants.FILE_EXTENSION);
        File dir1_vocab12 = new File(root_dir1, "dir1_vocab12." + ConfigConstants.FILE_EXTENSION);

        File dir1_dir11 = new File(root_dir1, "dir1_dir11");
        File dir11_vocab111 = new File(dir1_dir11, "dir11_vocab111." + ConfigConstants.FILE_EXTENSION);

        SampleModel() {
            root.mkdirs();
            root_dir1.mkdirs();
            root_dir2.mkdirs();
            root_dir3Empty.mkdirs();
            dir1_dir11.mkdirs();

            makeFile(dir2_vocab21);

            makeFile(root_vocab1);
            makeFile(root_vocab2);

            makeFile(dir1_vocab11);
            makeFile(dir1_vocab12);

            makeFile(dir11_vocab111);

            this.lessonModel = new LessonModel(root);
        }

        List<File> getAllFiles() {
            List<File> allFilesMutable = new ArrayList<File>();
            allFilesMutable.addAll(
                    Arrays.asList(
                            root,
                            root_dir1,
                            root_dir2,
                            dir2_vocab21,
                            root_dir3Empty,
                            root_vocab1,
                            root_vocab2,
                            dir1_vocab11,
                            dir1_vocab12,
                            dir1_dir11,
                            dir11_vocab111
                    )
            );
            return allFilesMutable;
        }

        void assertFilesRef(List<File> filesRefList) {
            List<File> mutableFilesRefList = new ArrayList<File>();
            mutableFilesRefList.addAll(filesRefList);
            assertFilesRef(lessonModel.getRoot(), mutableFilesRefList);
            if (mutableFilesRefList.size() > 0) {
                for (File file : mutableFilesRefList) {
                    System.out.println("In reference remaining: " + file);
                }
                assertTrue(
                        "In reference remaining entries: " + mutableFilesRefList.size(),
                        mutableFilesRefList.isEmpty()
                );
            }
        }

        void assertFilesRef(LessonContainer container, List<File> mutableFilesRefList) {
            for (Lesson lesson : container.lessons()) {
                if (lesson.isContainer()) {
                    assertFilesRef(lesson.asContainer(), mutableFilesRefList);
                } else {
                    assertTrue(
                            "Reference does not contain file: " + lesson.getFileRef().getName(),
                            mutableFilesRefList.remove(lesson.getFileRef())
                    );
                }
            }

            assertTrue(
                    "Reference does not contain directory: " + container.getFileRef().getName(),
                    mutableFilesRefList.remove(container.getFileRef())
            );
        }


        void makeFile(File file) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                try {
                    out.write(file.getCanonicalPath().getBytes("utf-8"));
                } finally {
                    out.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void removeSampleModel() {
            removeDirectory(lessonModel.getRoot().getFileRef());
        }

        void removeDirectory(File directory) {
            if (!directory.exists())
                return;

            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory())
                        removeDirectory(file);
                    else
                        assertTrue(file.delete());
                }
            }

            assertTrue(directory.delete());
        }

        LessonModel getLessonModel() {
            return lessonModel;
        }
    }
}
