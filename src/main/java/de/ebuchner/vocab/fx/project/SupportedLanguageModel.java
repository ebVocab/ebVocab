package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.config.VocabLanguage;
import de.ebuchner.vocab.config.VocabLanguages;

import java.util.*;

public class SupportedLanguageModel {

    private List<VocabLanguage> vocabLanguages;

    public SupportedLanguageModel() {
        TreeSet<VocabLanguage> sortedLanguages = new TreeSet<>(new VocabLanguageComparator());
        sortedLanguages.addAll(VocabLanguages.loadVocabLanguages());
        vocabLanguages = new ArrayList<>(sortedLanguages);
    }

    public Collection<VocabLanguage> languages() {
        return Collections.unmodifiableCollection(vocabLanguages);
    }

    private class VocabLanguageComparator implements Comparator<VocabLanguage> {
        public int compare(VocabLanguage o1, VocabLanguage o2) {
            int result = Boolean.valueOf(o2.isKnownTemplate()).compareTo(o1.isKnownTemplate());
            if (result == 0)
                result = o1.getDisplayName().compareTo(o2.getDisplayName());

            return result;
        }
    }
}
