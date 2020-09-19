package de.ebuchner.vocab.batch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.ebuchner.vocab.model.io.VocabIOHelper;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

public class VocabZipToJSON {

    public static final String PROJECT_DIR = "C:\\Users\\eb_on\\Dropbox\\eb\\bharat\\Language\\Hindi";
    public static final String OUTPUT_DIR = "C:\\Users\\eb_on\\Desktop\\testVocab";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    Stack<String> subDir = new Stack<>();


    public static void main(String[] args) {
        ProjectConfiguration.startupWithProjectDirectory(new File(PROJECT_DIR));

        new VocabZipToJSON().convertVocabDir();
    }

    private void convertVocabDir() {
        convertVocabDir(new File(PROJECT_DIR, "vocab"));
    }

    private void convertVocabDir(File rootDir) {

        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    subDir.push(f.getName());
                    convertVocabDir(f);
                    subDir.pop();
                } else
                    convertVocabFile(f);
            }
        }
    }

    private void convertVocabFile(File vocabFile) {
        try {
            if (VocabIOHelper.isRefFile(vocabFile)) {
                convertVocabRefFile(vocabFile);
                return;
            }

            VocabEntryList vocabList = VocabIOHelper.fromFile(vocabFile);
            System.out.format("\nFile %s - %d entries - %s\n", vocabFile.getName(), vocabList.entryCount(), subDir);

            StringBuilder outDirName = new StringBuilder(OUTPUT_DIR);
            Enumeration<String> en = subDir.elements();
            while (en.hasMoreElements()) {
                outDirName.append("\\").append(en.nextElement());
            }
            File outDir = new File(outDirName.toString());
            if (!outDir.exists() && !outDir.mkdirs())
                throw new RuntimeException("Could not create " + outDir);

            String jsonName = vocabFile.getName().substring(0, vocabFile.getName().length() - ".vocab".length()) + ".json";
            File outFile = new File(outDir, jsonName);

            try (FileWriter w = new FileWriter(outFile)) {
                gson.toJson(new WordList(vocabList), w);
            }

        } catch (Exception e) {
            //System.out.format("\nError File %s - %s", vocabFile.getName(), e.toString());
            // System.out.format("\nError File %s", vocabFile.getName());
        }
    }

    private void convertVocabRefFile(File vocabRefFile) throws IOException {
        List<VocabEntryRef> vocabRefList = VocabIOHelper.fromRefFile(vocabRefFile);
        System.out.format("\nREF File %s - %d entries - %s\n", vocabRefFile.getName(), vocabRefList.size(), subDir);

        StringBuilder outDirName = new StringBuilder(OUTPUT_DIR);
        Enumeration<String> en = subDir.elements();
        while (en.hasMoreElements()) {
            outDirName.append("\\").append(en.nextElement());
        }
        File outDir = new File(outDirName.toString());
        if (!outDir.exists() && !outDir.mkdirs())
            throw new RuntimeException("Could not create " + outDir);

        String jsonName = vocabRefFile.getName().substring(0, vocabRefFile.getName().length() - ".vocab_ref".length()) + ".ref.json";
        File outFile = new File(outDir, jsonName);

        try (FileWriter w = new FileWriter(outFile)) {
            gson.toJson(new WordReferenceList(vocabRefList), w);
        }
    }

    class WordList {
        List<Word> WordList = new ArrayList<>();

        WordList(VocabEntryList list) {
            for (VocabEntry entry : list.entries()) {
                WordList.add(new Word(entry));
            }
        }
    }

    class Word {
        long Id;
        String Term;
        String Translation;
        String Comment;
        String Type;

        Word(VocabEntry entry) {
            this.Id = Long.parseLong(entry.getId(), 16);
            this.Term = entry.getFieldValue("Foreign");
            this.Translation = entry.getFieldValue("User");
            this.Comment = entry.getFieldValue("Comment");
            this.Type = entry.getFieldValue("type");
        }
    }

    class WordReference {
        private final String referencedId;
        private final String relativeFilename;

        WordReference(VocabEntryRef ref) {
            this.referencedId = ref.getId();
            this.relativeFilename = ref.getFileRefString().substring(0, ref.getFileRefString().length() - ".vocab".length());
        }
    }

    class WordReferenceList {
        List<WordReference> wordReferences = new ArrayList<>();

        WordReferenceList(List<VocabEntryRef> refList) {
            for (VocabEntryRef ref : refList) {
                wordReferences.add(new WordReference(ref));
            }
        }
    }
}
