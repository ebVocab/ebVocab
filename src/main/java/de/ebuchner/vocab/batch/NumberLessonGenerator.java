package de.ebuchner.vocab.batch;

import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.model.io.VocabIOHelper;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class NumberLessonGenerator {

    private File inputDirectory;

    public NumberLessonGenerator(File inputDirectory) throws Exception {
        this.inputDirectory = inputDirectory;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            return;

        File projectDir = null;
        File inputDirectory = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-projectDir"))
                projectDir = new File(args[i + 1]);
            if (args[i].equals("-inputDirectory"))
                inputDirectory = new File(args[i + 1]);
        }

        ProjectConfiguration.startupWithProjectDirectory(projectDir);

        new NumberLessonGenerator(inputDirectory).generateDigits();
    }

    private void generateDigits() throws Exception {
        Map<Integer, VocabEntry> numberMap = new TreeMap<Integer, VocabEntry>();

        for (File vocabFile : inputDirectory.listFiles()) {
            generateDigits(vocabFile, numberMap);
        }

        for (int digit = 1; digit <= 9; digit++) {
            VocabEntryList outputEntryList = new VocabEntryList();
            for (int base = 0; base < 100; base += 10) {
                VocabEntry inputEntry = numberMap.get(base + digit);
                VocabEntry outputEntry = new VocabEntry();
                outputEntry.putFieldValue(
                        FieldFactory.USER,
                        inputEntry.getFieldValue(
                                FieldFactory.USER
                        )
                );
                outputEntry.putFieldValue(
                        FieldFactory.FOREIGN,
                        inputEntry.getFieldValue(
                                FieldFactory.FOREIGN
                        )
                );
                outputEntry.putFieldValue(
                        FieldFactory.COMMENT,
                        inputEntry.getFieldValue(
                                FieldFactory.COMMENT
                        )
                );
                outputEntryList.addEntry(outputEntry);
            }
            VocabIOHelper.toFileWithImage(
                    new File(inputDirectory, "Numbers " + digit + "x.vocab"),
                    outputEntryList
            );
        }
    }

    private void generateDigits(File vocabFile, Map<Integer, VocabEntry> numberMap) {
        VocabEntryList inputList = VocabIOHelper.fromFile(vocabFile);
        VocabEntryList outputList = new VocabEntryList();
        for (VocabEntry inputEntry : inputList.entries()) {
            VocabEntry outputEntry = new VocabEntry();
            outputList.addEntry(outputEntry);

            String hindiDigit = inputEntry.getFieldValue(FieldFactory.COMMENT);
            if (hindiDigit.length() < 1 || hindiDigit.length() > 2) {
                throw new RuntimeException("Illegal number: " + hindiDigit + " in " + vocabFile);
            }

            outputEntry.putFieldValue(
                    FieldFactory.FOREIGN,
                    inputEntry.getFieldValue(FieldFactory.COMMENT)
            );

            outputEntry.putFieldValue(
                    FieldFactory.USER,
                    inputEntry.getFieldValue(FieldFactory.USER)
            );

            outputEntry.putFieldValue(
                    FieldFactory.COMMENT,
                    inputEntry.getFieldValue(FieldFactory.FOREIGN)
            );

            String numberValueString = inputEntry.getFieldValue(FieldFactory.USER);
            StringTokenizer tokenizer = new StringTokenizer(numberValueString, "-");
            String numberString = tokenizer.nextToken().trim();
            int numberValue = Integer.parseInt(numberString);
            numberMap.put(numberValue, inputEntry);

        }

        /*
        VocabIOHelper.toFileWithImage(
                new File(vocabFile.getParent(), "Digits " + vocabFile.getName()),
                outputList
        );
        */
    }

}
