package de.ebuchner.vocab.model.search;

import junit.framework.TestCase;

public class TextSearcherTest extends TestCase {

    public void testPlain() {
        SearchOptions searchOptions = new SearchOptions();
        testBasics(searchOptions);

        assertMatches("test", "te", searchOptions);
        assertMatches("test", "es", searchOptions);
        assertMatches("test", "st", searchOptions);
        assertMatches(" test", "test ", searchOptions);

        assertMatchesNot("test", "tset", searchOptions);
        assertMatchesNot("test", "xtest", searchOptions);
        assertMatchesNot("test", "testx", searchOptions);
        assertMatchesNot("test", "xyz", searchOptions);
        assertMatches("test", "Test", searchOptions);
        assertMatches("test", "tEst", searchOptions);
        assertMatchesNot("test", "t.*", searchOptions);

        assertMatches("two words", "two", searchOptions);
        assertMatches("two words", "words", searchOptions);
        assertMatches("two words", "wo wo", searchOptions);
        assertMatches("two words", "TWO words", searchOptions);
        assertMatches("two words", "twO WoRdS", searchOptions);
        assertMatchesNot("two words", "twowords", searchOptions);
    }

    private void testBasics(SearchOptions searchOptions) {
        assertMatchesNot("", "", searchOptions);
        assertMatchesNot(" ", "", searchOptions);
        assertMatchesNot("", " ", searchOptions);
        assertMatchesNot("", null, searchOptions);
        assertMatchesNot(" ", null, searchOptions);
        assertMatchesNot(null, "", searchOptions);
        assertMatchesNot(null, " ", searchOptions);
        assertMatchesNot(null, null, searchOptions);

        assertMatches("test", "test", searchOptions);
    }

    public void testWholeWords() {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setWholeWords(true);
        testBasics(searchOptions);

        assertMatchesNot("test", "tes", searchOptions);
        assertMatchesNot("test", "est", searchOptions);

        assertMatches("all three words", "all", searchOptions);
        assertMatches("all three words", "three", searchOptions);
        assertMatches("all three words", "words", searchOptions);
        assertMatchesNot("all three words", "hree", searchOptions);
        assertMatchesNot("all three words", "ee w", searchOptions);
    }

    public void testCaseSensitive() {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setCaseSensitive(true);
        testBasics(searchOptions);

        assertMatchesNot("test", "TEST", searchOptions);
        assertMatchesNot("test", "Test", searchOptions);
        assertMatchesNot("test", "tEst", searchOptions);

        assertMatches("two words", "two", searchOptions);
        assertMatches("two words", "words", searchOptions);
        assertMatches("two words", "wo wo", searchOptions);
        assertMatchesNot("two words", "TWO words", searchOptions);
        assertMatchesNot("two words", "twO WoRdS", searchOptions);
        assertMatchesNot("two words", "twowords", searchOptions);
    }

    public void testRegEx() {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setRegularExpression(true);
        testBasics(searchOptions);

        assertMatches("test", "TEST", searchOptions);
        assertMatches("test", "T.*", searchOptions);
        assertMatches("test", "T[e]", searchOptions);
        assertMatchesNot("test", "T[abcd]", searchOptions);
    }

    public void testHindi() {
        String user = "सुस्ता";
        String comment = "सुस्त - lazy, sluggish";
        String searchString = "सुस्त";
        String searchString2 = "सु";

        SearchOptions searchOptions = new SearchOptions();
        assertMatches(user, searchString, searchOptions);
        assertMatches(comment, searchString, searchOptions);

        // Vokalzeichen werden bei "whole words" ignoriert
        searchOptions.setWholeWords(true);
        assertMatches(user, searchString, searchOptions);
        assertMatchesNot(user, searchString2, searchOptions);
        assertMatches(comment, searchString, searchOptions);
        assertMatchesNot(comment, searchString2, searchOptions);

        searchOptions.setCaseSensitive(true);
        searchOptions.setWholeWords(false);
        assertMatches(user, searchString, searchOptions);
        assertMatches(comment, searchString, searchOptions);
    }

    private void assertMatches(String test, String expression, SearchOptions searchOptions) {
        TextSearcher searcher = new TextSearcher(searchOptions, expression);
        assertTrue(searcher.matches(test));
    }


    private void assertMatchesNot(String test, String expression, SearchOptions options) {
        TextSearcher searcher = new TextSearcher(options, expression);
        assertFalse(searcher.matches(test));
    }
}
