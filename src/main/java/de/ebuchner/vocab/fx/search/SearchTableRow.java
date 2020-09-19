package de.ebuchner.vocab.fx.search;

import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;

public class SearchTableRow {

    private final VocabEntryRef entryRef;
    private final VocabEntry entry;

    public SearchTableRow(VocabEntryRef entryRef, VocabEntry entry) {
        this.entryRef = entryRef;
        this.entry = entry;
    }

    public String getForeign() {
        return entry.getFieldValue(FieldFactory.FOREIGN);
    }

    public String getUser() {
        return entry.getFieldValue(FieldFactory.USER);
    }

    public String getFileName() {
        if (entryRef.getFileRef() == null)
            return "";
        return entryRef.getFileRef().getName();
    }

    public VocabEntry getEntry() {
        return entry;
    }
}
