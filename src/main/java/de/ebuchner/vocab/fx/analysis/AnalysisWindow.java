package de.ebuchner.vocab.fx.analysis;

import de.ebuchner.vocab.fx.common.ContextMenuWindowParameter;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.model.analysis.AnalysisController;
import de.ebuchner.vocab.model.analysis.AnalysisWindowBehaviour;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.NuiWindowParameter;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.nui.focus.FocusAware;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AnalysisWindow extends FxBaseWindow implements FocusAware {
    @FXML
    private TextField tfInput;
    @FXML
    private TextField tfResult;
    @FXML
    private TextField tfUnicode;
    private AnalysisController controller = new AnalysisController(new MyBehaviour());
    private boolean listenForFocusEvents = true;

    @Override
    public void nuiWindowReceive(NuiWindowParameter windowParameter) {
        if (!(windowParameter instanceof ContextMenuWindowParameter))
            return;

        tfInput.setText(((ContextMenuWindowParameter) windowParameter).getContextFieldValue());
        onConvert(null);
        listenForFocusEvents = false;
    }

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.analysis.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "analysis";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {

    }

    @Override
    public WindowType windowType() {
        return WindowType.ANALYSIS_WINDOW;
    }

    public void onConvert(ActionEvent actionEvent) {
        controller.onConvert(tfInput.getText());
    }

    public void onClose(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    public void onFocusLost() {
    }

    public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {
        if (!listenForFocusEvents)
            return;

        String text = textFieldWithFocus.getText();
        if (text != null && text.trim().length() > 0) {
            tfInput.setText(text);
            onConvert(null);
        }
    }

    @Override
    protected void onStageCreated() {
        if (!listenForFocusEvents)
            return;

        tfInput.requestFocus();

        NuiTextFieldWithFocus nuiTextFieldWithFocus =
                UIPlatformFactory.getUIPlatform().getNuiDirector().getLastTextFieldWithFocus();
        if (nuiTextFieldWithFocus != null)
            onFocusChangedTo(nuiTextFieldWithFocus);
    }

    class MyBehaviour implements AnalysisWindowBehaviour {
        @Override
        public void showResult(String result) {
            tfResult.setText(result);
        }

        @Override
        public void showUnicode(String unicode) {
            tfUnicode.setText(unicode);
        }

    }
}
