package de.ebuchner.vocab.fx.alphabet;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.alphabet.AlphabetCharacter;
import de.ebuchner.vocab.model.alphabet.AlphabetModel;
import de.ebuchner.vocab.model.alphabet.RandomAlphabet;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlphabetWindow extends FxBaseWindow {
    private static final String QUESTION_MARK = "?";
    private static final String COLOR_STYLE_ERROR = "alphabet-error";

    @FXML
    private GridPane randomGrid;
    @FXML
    private GridPane solutionGrid;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnAll;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnClose;

    private AlphabetModel alphabet;
    private RandomAlphabet randomAlphabet;
    private AlphabetController alphabetController = new AlphabetController();
    private Map<AlphabetCharacter, Button> randomControls = new HashMap<>();
    private List<Button> alphabetControls = new ArrayList<>();
    private int position = 0;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.alphabet.title";
    }

    @Override
    protected void onStageCreated() {
        alphabet = AlphabetModel.getOrCreateAlphabetModel();
        randomAlphabet = new RandomAlphabet(alphabet);

        initGrid(GridType.RANDOM);
        initGrid(GridType.SOLUTION);
    }

    private void initGrid(GridType gridType) {
        boolean random = gridType == GridType.RANDOM;
        int x = 0;
        int y = 0;
        int pos = 0;

        for (AlphabetCharacter character : random ? randomAlphabet : alphabet) {
            Button button = new Button();
            button.setText(random ? character.getCharacter() : QUESTION_MARK);
            button.getStyleClass().add("modifiableFont");
            if (random) {
                button.setTooltip(new Tooltip(character.getDescription()));
                button.setOnAction(event -> alphabetController.onRandomButtonPressed(character));
                randomGrid.add(button, x, y);
                randomControls.put(character, button);
            } else {
                final int finalPos = pos;
                button.setOnAction(event -> alphabetController.onAlphabetButtonPressed(finalPos));
                button.setDisable(true);
                solutionGrid.add(button, x, y);
                alphabetControls.add(button);
            }
            GridPane.setHalignment(button, HPos.CENTER);

            x++;
            if (x > 10) {
                x = 0;
                y++;
            }
            pos++;
        }
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "alphabet";
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
        return WindowType.ALPHABET_WINDOW;
    }

    public void onNext(ActionEvent actionEvent) {
        alphabetController.onNext();
    }

    public void onAll(ActionEvent actionEvent) {
        alphabetController.onAll();
    }

    public void onReset(ActionEvent actionEvent) {
        alphabetController.onReset();
    }

    public void onClose(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    private static enum GridType {
        RANDOM, SOLUTION
    }

    // todo refactor split into model and window controller and move model controller to core
    class AlphabetController {
        public void onRandomButtonPressed(AlphabetCharacter character) {

            Button randomControl = randomControls.get(character);
            randomControl.setDisable(true);

            Button alphabetControl = alphabetControls.get(position);
            alphabetControl.setText(character.getCharacter());
            alphabetControl.setTooltip(new Tooltip(character.getDescription()));
            alphabetControl.setDisable(false);

            if (alphabet.characterAt(position) == character)
                alphabetControl.getStyleClass().remove(COLOR_STYLE_ERROR);
            else
                alphabetControl.getStyleClass().add(COLOR_STYLE_ERROR);

            position++;

            positionChanged();

            stage.sizeToScene();

            if (position >= alphabet.size()) {
                FxDialogs.create()
                        .title(i18n.getString("nui.alphabet.title"))
                        .message(i18n.getString("nui.alphabet.congratulation"))
                        .showInformation();
                onReset();
            }
        }

        void onReset() {
            for (Button button : randomControls.values()) {
                button.setDisable(false);
            }

            for (Button button : alphabetControls) {
                button.setText(QUESTION_MARK);
                button.setTooltip(null);
                button.setDisable(false);
                button.getStyleClass().removeAll(COLOR_STYLE_ERROR);
            }

            position = 0;
            positionChanged();

            stage.sizeToScene();
        }

        void positionChanged() {
            btnNext.setDisable(position >= alphabet.size());
            btnReset.setDisable(position == 0);
            btnAll.setDisable(position >= alphabet.size());
        }

        void onAlphabetButtonPressed(int characterPosition) {
            while (position >= characterPosition) {
                if (position < alphabet.size()) {
                    Button alphabetControl = alphabetControls.get(position);
                    String characterText = alphabetControl.getText();
                    alphabetControl.setText(QUESTION_MARK);
                    alphabetControl.setTooltip(null);
                    alphabetControl.getStyleClass().remove(COLOR_STYLE_ERROR);
                    alphabetControl.setDisable(true);

                    AlphabetCharacter character = null;

                    for (AlphabetCharacter rCharacter : randomAlphabet) {
                        if (rCharacter.getCharacter().equals(characterText)) {
                            character = rCharacter;
                            break;
                        }
                    }

                    Button randomControl = randomControls.get(character);
                    if (randomControl != null)
                        randomControl.setDisable(false);
                }
                position--;
            }
            position++;

            positionChanged();

            stage.sizeToScene();
        }

        void onNext() {
            Button alphabetControl = alphabetControls.get(position);
            AlphabetCharacter character = alphabet.characterAt(position);

            alphabetControl.setText(character.getCharacter());
            alphabetControl.setTooltip(new Tooltip(character.getDescription()));
            alphabetControl.getStyleClass().remove(COLOR_STYLE_ERROR);
            alphabetControl.setDisable(false);

            Button randomControl = randomControls.get(character);
            randomControl.setDisable(true);

            position++;
            positionChanged();

            stage.sizeToScene();
        }

        void onAll() {
            position = alphabet.size();
            positionChanged();

            for (int i = 0; i < alphabet.size(); i++) {
                AlphabetCharacter character = alphabet.characterAt(i);
                Button button = alphabetControls.get(i);
                button.setText(character.getCharacter());
                button.setTooltip(new Tooltip(character.getDescription()));
                button.getStyleClass().remove(COLOR_STYLE_ERROR);
                button.setDisable(false);
            }
            for (Button button : randomControls.values()) {
                button.setDisable(true);
            }
            stage.sizeToScene();
        }

    }
}
