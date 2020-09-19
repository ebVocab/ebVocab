package de.ebuchner.vocab.batch;

import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.model.io.VocabIOHelper;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;

public class BatchFileConverter {
    public static void main(String args[]) {
        if (args.length < 3)
            throw new IllegalArgumentException("Missing parameter for source and target directories and a valid project directory");

        File source = new File(args[0]);
        if (!source.exists())
            throw new IllegalArgumentException(args[0] + " does not exist");
        if (!source.isDirectory())
            throw new IllegalArgumentException(args[0] + " is not a directory");

        File target = new File(args[1]);
        if (target.exists())
            throw new IllegalArgumentException(args[1] + " already exists. Please choose a new directory name");

        File projectDir = new File(args[2]);
        if (!projectDir.exists())
            throw new IllegalArgumentException(args[2] + " does not exist");
        if (!projectDir.isDirectory())
            throw new IllegalArgumentException(args[2] + " is not a directory");
        ProjectConfiguration.startupWithProjectDirectory(projectDir);

        boolean withImage = false;
        for (int pos = 3; pos < args.length; pos++) {
            if (args[pos].equals("-withImage"))
                withImage = true;
        }

        System.out.println("Conversion option images: " + withImage);

        new BatchFileConverter().doConvertDirectory(source, target, withImage);
    }

    private void doConvertDirectory(File source, File target, boolean withImage) {
        if (!target.mkdirs())
            throw new RuntimeException("Could not create " + target);

        System.out.println("Converting directories: " + source.getName() + " to " + target.getName());

        File[] listFiles = source.listFiles();
        if (listFiles != null)
            for (File element : listFiles) {
                if (element.isDirectory()) {
                    doConvertDirectory(element, new File(target, element.getName()), withImage);
                } else {
                    doConvertFile(element, new File(target, element.getName()), withImage);
                }
            }
    }

    private void doConvertFile(File source, File target, boolean withImage) {
        if (!source.getName().endsWith("." + ConfigConstants.FILE_EXTENSION)) {
            System.out.println("Skipping " + source);
            return;
        }

        System.out.println("Converting file: " + source.getName());

        VocabEntryList entryList = VocabIOHelper.fromFile(source);
        if (withImage)
            VocabIOHelper.toFileWithImage(target, entryList);
        else
            VocabIOHelper.toFile(target, entryList);
    }

}
