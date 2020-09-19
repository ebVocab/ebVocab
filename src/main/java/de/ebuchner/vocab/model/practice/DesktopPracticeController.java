package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.model.nui.NuiWindow;
import de.ebuchner.vocab.model.nui.NuiWindowWithResult;
import de.ebuchner.vocab.model.nui.WindowType;

import java.io.File;

public class DesktopPracticeController extends PracticeController {
    public DesktopPracticeController(PracticeWindowBehaviour practiceWindow) {
        super(practiceWindow);
    }

    public void onRepetitionLoad() {
        NuiWindow window = super.onOpenWindowType(WindowType.REPETITION_LOAD_WINDOW);
        if (window instanceof NuiWindowWithResult) {
            File repetitionFile =
                    (File) ((NuiWindowWithResult) window).getResult().get(RepetitionLoadModel.REPETITION_FILE);
            if (repetitionFile != null)
                super.onChangeRepetitionImpl(repetitionFile);
        }
    }
}
