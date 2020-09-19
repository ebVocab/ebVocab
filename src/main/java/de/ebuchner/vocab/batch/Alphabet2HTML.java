package de.ebuchner.vocab.batch;

import de.ebuchner.vocab.model.io.VocabIOHelper;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Alphabet2HTML {

    private static final Logger LOGGER = Logger.getLogger(Alphabet2HTML.class.getName());

    private Alphabet2HTML() {

    }

    public static void main(String[] args) throws Exception {
        File vocabFile = null;
        File projectDir = null;
        File outputFile = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-vocabFile":
                    vocabFile = new File(args[i + 1]);
                    break;
                case "-projectDir":
                    projectDir = new File(args[i + 1]);
                    break;
                case "-outputFile":
                    outputFile = new File(args[i + 1]);
                    break;
            }
        }

        if (vocabFile == null || !vocabFile.exists() || vocabFile.isDirectory())
            throw new IllegalArgumentException("Missing or invalid parameter -vocabFile");
        if (projectDir == null || !projectDir.exists() || projectDir.isFile())
            throw new IllegalArgumentException("Missing or invalid parameter -projectDir");
        if (outputFile == null || outputFile.isDirectory())
            throw new IllegalArgumentException("Missing or invalid parameter -outputFile");

        ProjectConfiguration.startupWithProjectDirectory(projectDir);
        VocabEntryList alphabetEntries = VocabIOHelper.fromFile(
                vocabFile
        );
        generateHTML(alphabetEntries, outputFile);
        LOGGER.log(Level.INFO, "Created {0}", outputFile.getCanonicalPath());
    }

    private static void generateHTML(VocabEntryList vocabEntryList, File outputFile) throws Exception {
        try (PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
            printWriter.println("<html>");
            generateHeader(printWriter);
            generateBody(printWriter, vocabEntryList);
            printWriter.println("</html>");
        }
    }

    private static void generateTable(PrintWriter printWriter, VocabEntryList vocabEntryList) {
        printWriter.println("<table>");
        printWriter.println("<tr>");
        printWriter.println("<th>Character</th>");
        printWriter.println("<th>Description</th>");
        printWriter.println("</tr>");

        for (VocabEntry entry : vocabEntryList.entries()) {
            printWriter.println("<tr>");
            printWriter.println("<td><span style=\"font-family: Arial Unicode MS;\">${entry.getFieldValue(FieldFactory.FOREIGN)}</span></td>");
            printWriter.println("<td><span style=\"font-family: Arial Unicode MS;\">${entry.getFieldValue(FieldFactory.USER)}</span></td>");
            printWriter.println("</tr>");
        }

        printWriter.println("</table>");
    }

    private static void generateBody(PrintWriter printWriter, VocabEntryList vocabEntryList) {
        printWriter.println("<body>");
        generateTable(printWriter, vocabEntryList);
        printWriter.println("</body>");
    }

    private static void generateHeader(PrintWriter printWriter) {
        printWriter.println("<header>");
        printWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        printWriter.println("</header>");
    }
}
