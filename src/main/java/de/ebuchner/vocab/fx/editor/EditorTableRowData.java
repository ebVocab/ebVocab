package de.ebuchner.vocab.fx.editor;

import de.ebuchner.vocab.model.lessons.entry.VocabEntry;

public class EditorTableRowData {
    int row;
    VocabEntry vocabEntry;
    String translation;

    private EditorTableRowData(int row, VocabEntry vocabEntry, String translation) {
        this.row = row;
        this.vocabEntry = vocabEntry;
        this.translation = translation;
    }

    EditorTableRowData(int row, VocabEntry vocabEntry) {
        this(row, vocabEntry, null);
    }
}
