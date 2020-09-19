package de.ebuchner.sync.file;

import de.ebuchner.sync.*;
import junit.framework.TestCase;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class FileNodeTest extends TestCase {

    private static final String CHARSET_NAME = "utf-8";
    private File testBase;
    private File srcDir;
    private File targetDir;

    private Sync createSync() throws IOException {
        return new Sync(new DirectoryNode(null, srcDir), new DirectoryNode(null, targetDir));
    }

    @Override
    protected void setUp() throws Exception {
        testBase = createTestBase();
        srcDir = createDir(testBase, "src");
        targetDir = createDir(testBase, "target");
    }

    @Override
    protected void tearDown() throws Exception {
        if (testBase != null && testBase.exists())
            removeDir(testBase);
    }

    public void testFileSameFiles() throws IOException {
        createFile(srcDir, "file1.txt", 1);
        createFile(targetDir, "file1.txt", 1);
        Sync sync = createSync();

        sync.setSyncListener(new SyncListener() {
            int onc;
            int occ;

            @Override
            public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                onc++;
                assertEquals(SyncNodeComparator.ResultType.SAME, resultType);
            }

            @Override
            public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                occ++;
            }

            @Override
            public void onSourceOnly(SyncNode source) {
                fail("Should not call onSourceOnly");
            }

            @Override
            public void onTargetOnly(SyncNode target) {
                fail("Should not call onTargetOnly");
            }

            @Override
            public void onSyncStarted() {

            }

            @Override
            public void onSyncFinished() {
                assertEquals(1, onc);
                assertEquals(1, occ);
            }
        });
        sync.runCompare(new TestFileComparator());
    }

    public void testFileDifferentFiles() throws IOException {
        for (int i = 0; i < 2; i++) {
            final boolean snu = i == 0;
            createFile(srcDir, "file1.txt", snu ? 1 : 2);
            createFile(targetDir, "file1.txt", snu ? 2 : 1);
            Sync sync = createSync();

            sync.setSyncListener(new SyncListener() {
                int onc;
                int occ;

                @Override
                public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                    onc++;
                    assertEquals(
                            snu ?
                                    SyncNodeComparator.ResultType.SOURCE_NEEDS_UPDATE :
                                    SyncNodeComparator.ResultType.TARGET_NEEDS_UPDATE,
                            resultType
                    );
                }

                @Override
                public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                    occ++;
                }

                @Override
                public void onSourceOnly(SyncNode source) {
                    fail("Should not call onSourceOnly");
                }

                @Override
                public void onTargetOnly(SyncNode target) {
                    fail("Should not call onTargetOnly");
                }

                @Override
                public void onSyncStarted() {

                }

                @Override
                public void onSyncFinished() {
                    assertEquals(1, onc);
                    assertEquals(1, occ);
                }
            });
            sync.runCompare(new TestFileComparator());
        }
    }

    public void testSourceTargetFileMissing() throws IOException {
        for (int i = 0; i < 2; i++) {
            final boolean sourceOnly = i == 0;
            File file;
            if (sourceOnly)
                file = createFile(srcDir, "file1.txt", 1);
            else
                file = createFile(targetDir, "file1.txt", 2);

            Sync sync = createSync();
            sync.setSyncListener(new SyncListener() {
                int oso = 0;
                int oto = 0;
                int occ = 0;

                @Override
                public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                    fail("Should not call onNodesCompare");
                }

                @Override
                public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                    occ++;
                }

                @Override
                public void onSourceOnly(SyncNode source) {
                    oso++;
                    if (!sourceOnly)
                        fail("Should not call onSourceOnly");
                }

                @Override
                public void onTargetOnly(SyncNode target) {
                    oto++;
                    if (sourceOnly)
                        fail("Should not call onTargetOnly");
                }

                @Override
                public void onSyncStarted() {

                }

                @Override
                public void onSyncFinished() {
                    if (sourceOnly)
                        assertEquals(1, oso);
                    else
                        assertEquals(1, oto);
                    assertEquals(1, occ);
                }
            });
            sync.runCompare(new TestFileComparator());
            removeFile(file);
        }
    }

    public void testSourceTargetDirMissing() throws IOException {
        for (int i = 0; i < 2; i++) {
            final boolean sourceOnly = i == 0;
            File dir;
            if (sourceOnly)
                dir = createDir(srcDir, "subDir");
            else
                dir = createDir(targetDir, "subDir");

            Sync sync = createSync();
            sync.setSyncListener(new SyncListener() {
                int oso = 0;
                int oto = 0;
                int occ = 0;

                @Override
                public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                    fail("Should not call onNodesCompare");
                }

                @Override
                public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                    occ++;
                }

                @Override
                public void onSourceOnly(SyncNode source) {
                    oso++;
                    if (!sourceOnly)
                        fail("Should not call onSourceOnly");
                }

                @Override
                public void onTargetOnly(SyncNode target) {
                    oto++;
                    if (sourceOnly)
                        fail("Should not call onTargetOnly");
                }

                @Override
                public void onSyncStarted() {

                }

                @Override
                public void onSyncFinished() {
                    if (sourceOnly)
                        assertEquals(1, oso);
                    else
                        assertEquals(1, oto);
                    assertEquals(1, occ);
                }

            });
            sync.runCompare(new TestFileComparator());
            removeDir(dir);
        }
    }

    public void testDirFileSameName() throws IOException {
        createFile(srcDir, "x", 1);
        createDir(targetDir, "x");

        Sync sync = createSync();
        sync.setSyncListener(new SyncListener() {
            int oso = 0;
            int oto = 0;
            int occ = 0;

            @Override
            public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                fail("Should not call onNodesCompare");
            }

            @Override
            public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                occ++;
            }

            @Override
            public void onSourceOnly(SyncNode source) {
                oso++;
            }

            @Override
            public void onTargetOnly(SyncNode target) {
                oto++;
            }

            @Override
            public void onSyncStarted() {

            }

            @Override
            public void onSyncFinished() {
                assertEquals(1, occ);
                assertEquals(1, oso);
                assertEquals(1, oto);
            }

        });
        sync.runCompare(new TestFileComparator());
    }

    public void testSourceTargetDir() throws IOException {
        createDir(srcDir, "subDir");
        createDir(targetDir, "subDir");

        Sync sync = createSync();
        sync.setSyncListener(new SyncListener() {
            int occ = 0;

            @Override
            public void onNodesCompare(SyncNode source, SyncNode target, SyncNodeComparator.ResultType resultType) {
                fail("Should not call onNodesCompare");
            }

            @Override
            public void onNodeContainersCompare(SyncNodeContainer source, SyncNodeContainer target) {
                occ++;
            }

            @Override
            public void onSourceOnly(SyncNode source) {
                fail("Should not call onSourceOnly");
            }

            @Override
            public void onTargetOnly(SyncNode target) {
                fail("Should not call onTargetOnly");
            }

            @Override
            public void onSyncStarted() {

            }

            @Override
            public void onSyncFinished() {
                assertEquals(2, occ);
            }

        });
        sync.runCompare(new TestFileComparator());
    }


    private File createFile(File dir, String name, int version) throws IOException {
        final File file = new File(
                dir,
                name
        );
        try (
                Writer writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        file
                                ),
                                CHARSET_NAME
                        )
                )
        ) {
            writer.write(String.valueOf(version));
        }
        return file;
    }

    private void removeDir(File dir) {
        removeFile(dir);
    }

    private void removeFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    removeFile(child);
                }
            }
        }
        assertTrue("Could not delete " + file, file.delete());
    }

    private File createTestBase() {
        File testBase = new File(System.getProperty("java.io.tmpdir"), getClass().getName());
        if (testBase.exists())
            removeDir(testBase);

        assertTrue(testBase.mkdir());
        return testBase;
    }

    private File createDir(File parentDir, String name) {
        File dir = new File(parentDir, name);
        assertTrue(dir.mkdir());
        return dir;
    }

    private class TestFileComparator implements SyncNodeComparator {
        @Override
        public ResultType compareNodes(SyncNode source, SyncNode target) {
            assertTrue(source instanceof FileNode);
            assertTrue(target instanceof FileNode);

            FileNode sourceNode = (FileNode) source;
            FileNode targetNode = (FileNode) target;

            assertTrue(sourceNode.getFile().isFile());
            assertTrue(targetNode.getFile().isFile());

            int sourceVersion = readVersion(sourceNode.getFile());
            int targetVersion = readVersion(targetNode.getFile());

            if (sourceVersion == targetVersion)
                return ResultType.SAME;
            else if (sourceVersion < targetVersion)
                return ResultType.SOURCE_NEEDS_UPDATE;

            return ResultType.TARGET_NEEDS_UPDATE;
        }

        private int readVersion(File file) {
            assertTrue(file.exists());
            try {
                List<String> lines = Files.readAllLines(file.toPath(), Charset.forName(CHARSET_NAME));
                assertNotNull(lines);
                assertEquals(1, lines.size());
                return Integer.parseInt(lines.get(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}