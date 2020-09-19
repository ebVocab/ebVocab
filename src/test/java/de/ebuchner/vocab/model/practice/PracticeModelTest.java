package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.config.preferences.PreferenceValueList;
import junit.framework.TestCase;

public class PracticeModelTest extends TestCase {

    public void testStrategy() {
        PracticeModel model1 = new PracticeModel();
        model1.setSelectedStrategy(SelectedStrategy.INTENSE);
        model1.setReverse(PracticeReverse.REVERSE);
        model1.setShowLessonFile(true);

        PreferenceValueList values = new PreferenceValueList();
        model1.saveToPreferences(values);

        PracticeModel model2 = new PracticeModel();
        model2.restoreFromPreferences(values);

        assertEquals(SelectedStrategy.INTENSE, model2.getSelectedStrategy());
        assertEquals(PracticeReverse.REVERSE, model2.getReverse());
        assertTrue(model2.isShowLessonFile());
    }
}
