package de.ebuchner.vocab.model;

import de.ebuchner.vocab.model.lessons.LessonModel;
import de.ebuchner.vocab.nui.NuiStarter;
import junit.framework.TestCase;

public class VocabModelTest extends TestCase {

    public void testLessonModel() {
        new NuiStarter().prepareProjectDir(NuiStarter.PreferencesHandling.RESET);

        VocabModel vocabModel = VocabModel.getInstance();
        assertNotNull(vocabModel);

        LessonModel lessonModel = LessonModel.getOrCreateLessonModel();
        assertNotNull(lessonModel);

        LessonModel lessonModel2 = LessonModel.getOrCreateLessonModel();
        assertSame(lessonModel, lessonModel2);

        vocabModel.savePreferences();
    }
}
