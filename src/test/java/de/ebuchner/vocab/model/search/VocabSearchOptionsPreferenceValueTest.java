package de.ebuchner.vocab.model.search;

import junit.framework.TestCase;

public class VocabSearchOptionsPreferenceValueTest extends TestCase {

    public void testSearchOptions() {
        VocabSearchOptions searchOptions = new VocabSearchOptions();
        searchOptions.setCaseSensitive(true);
        searchOptions.setRegularExpression(false);
        searchOptions.setWholeWords(true);
        searchOptions.addSearchField("field1");
        searchOptions.addSearchField("field2");
        searchOptions.addSearchField("field3");

        executeTest(searchOptions);
    }

    private void executeTest(VocabSearchOptions searchOptions) {
        VocabSearchOptionsPreferenceValue preferenceValue1 =
                new VocabSearchOptionsPreferenceValue(searchOptions);

        VocabSearchOptionsPreferenceValue preferenceValue2 = new VocabSearchOptionsPreferenceValue();

        preferenceValue2.fromString(preferenceValue1.asString());

        assertEquals(preferenceValue1.getSearchOptions(), preferenceValue2.getSearchOptions());

    }

    public void testEmptySearchOptions() {
        VocabSearchOptions searchOptions = new VocabSearchOptions();
        executeTest(searchOptions);
    }
}
