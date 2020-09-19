package de.ebuchner.vocab.fx.editor;

import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;

public class EditorTableRow {

    private final EditorTableRowData data;

    EditorTableRow(EditorTableRowData data) {
        this.data = data;
    }

    public String getForeign() {
        return data.vocabEntry.getFieldValue(FieldFactory.FOREIGN);
    }

    public String getType() {
        return data.vocabEntry.getFieldValue(FieldFactory.TYPE);
    }

    public String getUser() {
        return data.vocabEntry.getFieldValue(FieldFactory.USER);
    }

    public int getRowNumber() {
        return data.row;
    }

    public VocabEntry getEntry() {
        return data.vocabEntry;
    }

    public String getTranslation() {
        return data.translation;
    }

    public void setTranslation(String translation) {
        this.data.translation = translation;
    }
}
