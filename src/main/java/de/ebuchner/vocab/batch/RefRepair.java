package de.ebuchner.vocab.batch;

import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.config.ProjectInfo;
import de.ebuchner.vocab.model.io.VocabIOHelper;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RefRepair {

    private final static File PROJECT_DIR = new File("D:\\_home\\Dropbox\\eb\\bharat\\Language\\Hindi");

    public static void main(String[] args) throws IOException {
        ProjectConfiguration.startupWithProjectDirectory(PROJECT_DIR);
        ProjectInfo info = new ProjectInfo(PROJECT_DIR);

        handleDir(info.getVocabDirectory());
    }

    private static void handleDir(File directory) throws IOException {
        File[] content = directory.listFiles();
        if (content == null)
            return;

        for (File file : content) {
            if (file.isDirectory()) {
                handleDir(file);
                continue;
            }

            if (!file.getName().endsWith(ConfigConstants.FILE_REF_EXTENSION))
                continue;

            System.out.printf("Found ref file %s\n", file.getAbsolutePath());
            List<VocabEntryRef> refList = VocabIOHelper.fromRefFile(file);

            for (VocabEntryRef ref : refList) {
                File fileRef = ref.getFileRef();
                if (!fileRef.exists()) {
                    System.out.printf("  Invalid ref %s\n", fileRef.getPath());
                    continue;
                }

                if (new File(ref.getFileRefString()).isAbsolute())
                    System.out.printf("  Absolute ref %s\n", ref.getFileRefString());

            }

            //VocabIOHelper.toRefFile(file, refList);
        }

    }
}
