package de.ebuchner.vocab.fx.font;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.platform.FontLoader;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.font.*;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Set;
import java.util.TreeSet;

public class FontSelectionWindow extends FxBaseWindow {

    @FXML
    private ComboBox<String> cmbFonts;
    @FXML
    private ComboBox<Integer> cmbFontSizes;
    @FXML
    private ToggleButton btnBold;
    @FXML
    private ToggleButton btnItalic;
    @FXML
    private TextField tfTest;

    private boolean fontModified = false;
    private Font selectedFont;
    private FontSelectionWindowController controller = new FontSelectionWindowController();

    @Override
    protected void onStageCreated() {
        initializeFonts();
        initializeFontSizes();

        String fontSample = FontModel.getOrCreateFontModel().getFontSample();
        tfTest.setText(fontSample);
        tfTest.setPrefColumnCount(fontSample.length() + 5);

        initUIFromFontModel();
    }

    private void initUIFromFontModel() {
        VocabFont currentVocabFont = FontModel.getOrCreateFontModel().getFont(VocabFontType.VOCABULARY);
        if (currentVocabFont == null)
            return;
        {
            cmbFonts.getSelectionModel().select(currentVocabFont.getName());
            // select(int) -> position <-> select(Integer) -> value
            cmbFontSizes.getSelectionModel().select(Integer.valueOf(currentVocabFont.getSize()));
            if (currentVocabFont.getStyle() == VocabFontStyle.BOLD || currentVocabFont.getStyle() == VocabFontStyle.BOLD_ITALIC)
                btnBold.setSelected(true);
            if (currentVocabFont.getStyle() == VocabFontStyle.ITALIC || currentVocabFont.getStyle() == VocabFontStyle.BOLD_ITALIC)
                btnItalic.setSelected(true);

            controller.onFontSelectionUpdated();
            fontModified = false;
        }
    }

    private void initializeFontSizes() {
        Set<Integer> fontSizes = new TreeSet<>();
        fontSizes.addAll(VocabFontSizes.fontSizes());
        cmbFontSizes.getItems().addAll(fontSizes);
    }

    private void initializeFonts() {
        AvailableFontsModel availableFontsModel = new AvailableFontsModel();
        cmbFonts.getItems().addAll(availableFontsModel.getFontNames());
        FontCellFactory.customize(cmbFonts);

    }

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.font.selection.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "font";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        if (closeType == NuiCloseEvent.CloseType.OK)
            return NuiClosingResult.CLOSING_OK;

        if (fontModified)
            return confirmCloseTypeCancel();

        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
        if (closeType == NuiCloseEvent.CloseType.OK && fontModified)
            FontModel.getOrCreateFontModel().changeFont(
                    VocabFontType.VOCABULARY,
                    FxUIPlatform.instance().toVocabFont(selectedFont)
            );
    }

    @Override
    protected boolean isVocabWindowModal() {
        return true;
    }

    @Override
    public WindowType windowType() {
        return WindowType.FONT_SELECTION_WINDOW;
    }

    public void onFontSelectionUpdated(ActionEvent actionEvent) {
        controller.onFontSelectionUpdated();
    }

    public void onOk(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.OK);
    }

    public void onCancel(ActionEvent actionEvent) {
        selectedFont = null;
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CANCEL);
    }

    public void onFontReset(ActionEvent actionEvent) {
        controller.onFontReset();
    }

    private class FontSelectionWindowController {

        void onFontSelectionUpdated() {
            if (cmbFonts.getSelectionModel().getSelectedItem() == null)
                return;
            if (cmbFontSizes.getSelectionModel().getSelectedItem() == null)
                return;

            fontModified = true;


            selectedFont = FontLoader.font(
                    cmbFonts.getSelectionModel().getSelectedItem(),
                    btnBold.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL,
                    btnItalic.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR,
                    cmbFontSizes.getSelectionModel().getSelectedItem()
            );

            tfTest.setFont(selectedFont);
            stage.sizeToScene();
        }

        void onFontReset() {
            FontModel.getOrCreateFontModel().resetFont(VocabFontType.VOCABULARY);
            windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CANCEL);
        }
    }
}
