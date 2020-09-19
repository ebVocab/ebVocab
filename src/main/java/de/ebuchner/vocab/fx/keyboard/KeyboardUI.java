package de.ebuchner.vocab.fx.keyboard;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.fx.common.Dimension;
import de.ebuchner.vocab.fx.common.Point;
import de.ebuchner.vocab.fx.common.Rectangle;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.font.FontModel;
import de.ebuchner.vocab.model.font.VocabFont;
import de.ebuchner.vocab.model.font.VocabFontType;
import de.ebuchner.vocab.model.keyboard.KeyMap;
import de.ebuchner.vocab.model.keyboard.KeyMapEntry;
import de.ebuchner.vocab.model.keyboard.KeyMode;
import de.ebuchner.vocab.model.keyboard.KeyModifier;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.ebuchner.vocab.model.keyboard.KeyModifier.NORMAL;
import static de.ebuchner.vocab.model.keyboard.KeyModifier.SHIFT;

public class KeyboardUI {

    private static final int NOT_SELECTED = -1;
    private static final int COLUMN_GAP = 5;
    private static final int ROW_GAP = 5;
    private static final int KEY_MARGIN = 5;
    private static final Color COLOR_KEY_BORDER = Color.web("0x000088");
    private static final Color COLOR_SELECTED_KEY_BACKGROUND = Color.WHITE;
    private static final Color COLOR_KEY_BACKGROUND = Color.BLACK;
    private static final Color COLOR_KEY_CHARACTER_ENABLED = Color.WHITE;
    private static final Color COLOR_KEY_CHARACTER_DISABLED = Color.DARKGRAY;
    private static final Color COLOR_KEY_CHARACTER_SELECTED_ENABLED = Color.BLACK;
    private static final Color COLOR_KEY_CHARACTER_SELECTED_DISABLED = Color.GRAY;
    private final Canvas cvKeyboard;
    private int selectedRow = NOT_SELECTED;
    private int selectedColumn = NOT_SELECTED;
    private KeyMap foreignKeyMap;
    private KeyMap userKeyMap;
    private List<KeyboardUIListener> listeners = new ArrayList<>();
    private KeyShapeCache keyShapeCache = new KeyShapeCache();
    // todo extract as Context or something
    private Font currentFont;
    private KeyModifier panelKeyModifier = NORMAL;
    private String selectedKeyString;
    private Dimension keySize;
    private Bounds currentKeyTextBounds;

    public KeyboardUI(Canvas cvKeyboard) {
        this(
                cvKeyboard,
                Config.instance().getLocale(),
                Config.instance().getSystemLocale()
        );
    }

    public KeyboardUI(Canvas cvKeyboard, String foreignLocale, String userLocale) {
        this.cvKeyboard = cvKeyboard;

        foreignKeyMap = KeyMap.fromLocale(foreignLocale);
        userKeyMap = KeyMap.fromLocale(userLocale);
        if (userKeyMap == null) {
            userKeyMap = KeyMap.fromLocale("en");
        }

        KeyHandler keyHandler = new KeyHandler();
        cvKeyboard.setOnKeyPressed(keyHandler);
        cvKeyboard.setOnKeyReleased(keyHandler);

        MouseHandler mouseHandler = new MouseHandler();
        cvKeyboard.setOnMouseClicked(mouseHandler);
        cvKeyboard.setOnMousePressed(mouseHandler);
        cvKeyboard.setOnMouseReleased(mouseHandler);
        cvKeyboard.setOnMouseEntered(mouseHandler);
        cvKeyboard.setOnMouseExited(mouseHandler);

        calculateKeyboardSize();

        drawOnCanvas();
    }

    private void calculateKeyboardSize() {
        VocabFont vocabFont = FontModel.getOrCreateFontModel().getFont(VocabFontType.VOCABULARY);
        if (vocabFont != null)
            currentFont = FxUIPlatform.instance().toFont(vocabFont);
        if (currentFont == null)
            currentFont = Font.getDefault();

        int rows = foreignKeyMap.keyMapEntries().stream().map(KeyMapEntry::getRow).collect(Collectors.toSet()).size();
        int columns = foreignKeyMap.keyMapEntries().stream().map(KeyMapEntry::getColumn).collect(Collectors.toSet()).size();

        Text text = new Text();
        text.setFont(currentFont);
        text.setText("W");
        currentKeyTextBounds = text.getLayoutBounds();

        keySize = new Dimension(
                3 * currentKeyTextBounds.getWidth(),
                2 * currentKeyTextBounds.getHeight()
        );

        cvKeyboard.setWidth(
                columns * keySize.getWidth() + (columns + 1) * COLUMN_GAP
        );
        cvKeyboard.setHeight(
                rows * keySize.getHeight() + (rows + 1) * ROW_GAP
        );
    }

    private void drawOnCanvas() {
        keyShapeCache.clear();

        fireFontChanged();

        // draw all keys
        foreignKeyMap.keyMapEntries().forEach(this::drawKey);

        userKeyMap.keyMapEntries().forEach(this::drawKey);

        // find text to generate for selected key
        for (KeyMapEntry entry : foreignKeyMap.keyMapEntries()) {
            if (entry.getRow() == selectedRow && entry.getColumn() == selectedColumn)
                selectedKeyString = entry.generatedTextForKeyModifier(panelKeyModifier);
        }
    }

    private void drawKey(KeyMapEntry entry) {
        drawKeyCharacters(drawKeyShape(entry), entry);
    }

    private Rectangle drawKeyShape(KeyMapEntry entry) {
        GraphicsContext g = cvKeyboard.getGraphicsContext2D();

        // draw key only once
        Rectangle keyShape = keyShapeCache.keyShapeOf(entry.getRow(), entry.getColumn());
        if (keyShape != null)
            return keyShape;

        double xPos = (entry.getColumn() + 1) * COLUMN_GAP + (entry.getColumn()) * keySize.getWidth();
        double yPos = (entry.getRow() + 1) * ROW_GAP + (entry.getRow()) * keySize.getHeight();

        keyShape = new Rectangle(xPos, yPos, keySize.getWidth(), keySize.getHeight());
        keyShapeCache.addKeyShape(entry.getRow(), entry.getColumn(), keyShape);

        if (entry.getRow() == selectedRow && entry.getColumn() == selectedColumn)
            g.setFill(COLOR_SELECTED_KEY_BACKGROUND);
        else
            g.setFill(COLOR_KEY_BACKGROUND);

        g.fillRoundRect(
                keyShape.getX(),
                keyShape.getY(),
                keySize.getWidth(),
                keySize.getHeight(),
                2 * KEY_MARGIN,
                2 * KEY_MARGIN
        );

        g.setFill(COLOR_KEY_BORDER);
        g.strokeRoundRect(
                keyShape.getX(),
                keyShape.getY(),
                keySize.getWidth(),
                keySize.getHeight(),
                2 * KEY_MARGIN,
                2 * KEY_MARGIN
        );

        return keyShape;
    }

    private void drawKeyCharacters(Rectangle keyShape, KeyMapEntry entry) {

        boolean keySelected = (entry.getRow() == selectedRow && entry.getColumn() == selectedColumn);

        for (KeyMode keyMode : entry.getKeyModes()) {
            KeyModifier keyModifier = keyMode.getModifier();

            switch (panelKeyModifier) {
                case ALT_GR:
                case SHIFT_ALT_GR:
                    if (keyModifier == panelKeyModifier) {
                        boolean keyEnabled = false;
                        CharacterPosition position = CharacterPosition.LOWER_LEFT;
                        if (entry.getLocale().equals(foreignKeyMap.getLocale())) {
                            keyEnabled = true;
                            position = CharacterPosition.LOWER_RIGHT;
                        }

                        drawEntryCharacter(
                                entry.generatedTextForKeyModifier(keyModifier),
                                keyShape,
                                position,
                                keyEnabled,
                                keySelected
                        );
                    }
                    break;
                case SHIFT:
                case NORMAL:
                    // Shift
                    if (keyModifier == SHIFT) {

                        boolean keyEnabled = false;
                        CharacterPosition position = CharacterPosition.UPPER_LEFT;
                        if (entry.getLocale().equals(foreignKeyMap.getLocale())) {
                            keyEnabled = panelKeyModifier == SHIFT;
                            position = CharacterPosition.UPPER_RIGHT;
                        }

                        drawEntryCharacter(
                                entry.generatedTextForKeyModifier(keyModifier),
                                keyShape,
                                position,
                                keyEnabled,
                                keySelected
                        );
                    }

                    // Normal
                    if (keyModifier == NORMAL && !entry.isCapitalOnly()) {

                        boolean keyEnabled = false;
                        CharacterPosition position = CharacterPosition.LOWER_LEFT;
                        if (entry.getLocale().equals(foreignKeyMap.getLocale())) {
                            keyEnabled = panelKeyModifier == NORMAL;
                            position = CharacterPosition.LOWER_RIGHT;
                        }

                        drawEntryCharacter(
                                entry.generatedTextForKeyModifier(keyModifier),
                                keyShape,
                                position,
                                keyEnabled,
                                keySelected
                        );
                    }
                    break;
            }
        }
    }

    private void fireFontChanged() {
        for (KeyboardUIListener listener : listeners) {
            listener.fontChanged(currentFont);
        }
    }

    public String keyboardName() {
        return null;
    }

    public void addListener(KeyboardUIListener listener) {
        listeners.add(listener);
    }

    private void fireKeyStringSelected() {
        for (KeyboardUIListener listener : listeners) {
            listener.keyTyped(selectedKeyString);
        }
    }

    private void drawEntryCharacter(
            String text,
            Rectangle keyShape,
            CharacterPosition position,
            boolean enabled,
            boolean selected
    ) {
        if (text == null)
            return;

        GraphicsContext g = cvKeyboard.getGraphicsContext2D();

        if (enabled)
            g.setFill(selected ? COLOR_KEY_CHARACTER_SELECTED_ENABLED : COLOR_KEY_CHARACTER_ENABLED);
        else
            g.setFill(selected ? COLOR_KEY_CHARACTER_SELECTED_DISABLED : COLOR_KEY_CHARACTER_DISABLED);

        switch (position) {
            case LOWER_LEFT:
            case UPPER_LEFT:
            case LOWER_RIGHT:
            case UPPER_RIGHT:
                int row = 0;
                if (position == CharacterPosition.LOWER_LEFT || position == CharacterPosition.LOWER_RIGHT)
                    row = 1;

                int col = 0;
                if (position == CharacterPosition.LOWER_RIGHT || position == CharacterPosition.UPPER_RIGHT)
                    col = 1;

                g.setFont(currentFont);
                double xPos = keyShape.getX() + KEY_MARGIN;
                double yPos = keyShape.getY() + KEY_MARGIN + (row + 0.5) * currentKeyTextBounds.getHeight();
                if (col == 1)
                    xPos = keyShape.getX() + keyShape.getWidth() - KEY_MARGIN - currentKeyTextBounds.getWidth();
                g.fillText(text, xPos, yPos);
                break;
        }
    }

    private static enum CharacterPosition {
        UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT
    }

    private class MouseHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
                mouseClicked(event);
            else if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
                mousePressed(event);
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
                mouseReleased(event);
            else if (event.getEventType() == MouseEvent.MOUSE_ENTERED)
                mouseEntered(event);
            else if (event.getEventType() == MouseEvent.MOUSE_EXITED)
                mouseExited(event);
        }

        void mouseClicked(MouseEvent e) {
            cvKeyboard.requestFocus();
        }

        void mousePressed(MouseEvent e) {
            Point pt = new Point(e.getSceneX(), e.getSceneY());

            selectedRow = keyShapeCache.rowFrom(pt, NOT_SELECTED);
            selectedColumn = keyShapeCache.columnFrom(pt, NOT_SELECTED);

            drawOnCanvas();
        }

        void mouseReleased(MouseEvent e) {
            if (selectedKeyString != null) {
                fireKeyStringSelected();
            }
            selectedKeyString = null;
            resetSelectedPos();
        }

        void mouseEntered(MouseEvent e) {
            resetSelectedPos();
        }

        void mouseExited(MouseEvent e) {
            resetSelectedPos();
        }

        private void resetSelectedPos() {
            selectedRow = NOT_SELECTED;
            selectedColumn = NOT_SELECTED;
            drawOnCanvas();
        }
    }

    private class KeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.getEventType() == KeyEvent.KEY_PRESSED)
                keyPressed(event);
            else if (event.getEventType() == KeyEvent.KEY_RELEASED)
                keyReleased(event);
        }

        void keyPressed(KeyEvent e) {
            KeyMapEntry selectedEntry = selectedForeignEntry(e);
            if (selectedEntry != null) {
                selectedRow = selectedEntry.getRow();
                selectedColumn = selectedEntry.getColumn();
            }

            panelKeyModifier = KeyEventHandler.fromKeyEvent(e).keyModifier();

            drawOnCanvas();
            e.consume();
        }

        KeyMapEntry selectedForeignEntry(KeyEvent e) {
            KeyMapEntry selectedUserEntry = null;

            for (KeyMapEntry entry : userKeyMap.keyMapEntries()) {
                for (KeyMode keyMode : entry.getKeyModes()) {
                    if (e.getCharacter().equals(keyMode.getGenerateText()))
                        selectedUserEntry = entry;
                }
            }

            if (selectedUserEntry == null)
                return null;

            KeyMapEntry selectedForeignEntry = null;
            for (KeyMapEntry entry : foreignKeyMap.keyMapEntries()) {
                if (entry.getRow() == selectedUserEntry.getRow() &&
                        entry.getColumn() == selectedUserEntry.getColumn())
                    selectedForeignEntry = entry;
            }

            return selectedForeignEntry;
        }

        void keyReleased(KeyEvent e) {
            panelKeyModifier = KeyEventHandler.fromKeyEvent(e).keyModifier();

            selectedRow = NOT_SELECTED;
            selectedColumn = NOT_SELECTED;

            KeyMapEntry selectedEntry = selectedForeignEntry(e);

            String string = null;
            KeyCode keyCode = null;

            if (selectedEntry != null) {
                string = selectedEntry.generatedTextForKeyModifier(panelKeyModifier);
            } else {
                if (KeyCode.SPACE == e.getCode())
                    string = " ";
                if (KeyCode.BACK_SPACE == e.getCode())
                    keyCode = KeyCode.BACK_SPACE;
            }

            for (KeyboardUIListener listener : listeners) {
                if (string != null)
                    listener.keyTyped(string);
                else if (keyCode != null)
                    listener.keyCode(keyCode);
            }

            drawOnCanvas();
            e.consume();
        }
    }
}
