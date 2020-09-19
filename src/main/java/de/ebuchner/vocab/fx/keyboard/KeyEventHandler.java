package de.ebuchner.vocab.fx.keyboard;

import de.ebuchner.vocab.model.keyboard.KeyModifier;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class KeyEventHandler {

    private static KeyEventHandler eventHandler = new KeyEventHandler();
    private List<EventModifierType> eventModifierTypes = new ArrayList<>();

    public static KeyEventHandler fromKeyEvent(KeyEvent event) {
        dump(event);

        if (event.getCode() == KeyCode.SHIFT)
            modifierTypesFromEvent(event, EventModifierType.SHIFT_LEFT, EventModifierType.SHIFT_RIGHT);

        if (event.getCode() == KeyCode.CONTROL)
            modifierTypesFromEvent(event, EventModifierType.CTRL_LEFT, EventModifierType.CTRL_RIGHT);

        if (event.getCode() == KeyCode.ALT)
            modifierTypesFromEvent(event, EventModifierType.ALT_LEFT);

        if (event.getCode() == KeyCode.ALT_GRAPH)
            modifierTypesFromEvent(event, EventModifierType.ALT_GR);

        /*
        StringBuilder tmp = new StringBuilder("Detected: ");
        for (EventModifierType type : eventHandler.eventModifierTypes) {
            tmp.append(" ").append(type.name());
        }
        System.out.println(tmp);
        */

        return eventHandler;
    }

    private static void modifierTypesFromEvent(KeyEvent event, EventModifierType... modifierTypes) {
        for (EventModifierType modifierType : modifierTypes) {
            if (event.getEventType() == KeyEvent.KEY_PRESSED)
                eventHandler.eventModifierTypes.add(modifierType);
            else
                eventHandler.eventModifierTypes.remove(modifierType);
        }
    }

    private static void dump(KeyEvent event) {
        final StringBuilder sb = new StringBuilder(event.getEventType().getName());

        //sb.append(", char=").append(event.getCharacter());
        //sb.append(", text=").append(event.getText());
        sb.append(", code=").append(event.getCode());

        if (event.isShiftDown()) {
            sb.append(", shift");
        }
        if (event.isControlDown()) {
            sb.append(", control");
        }
        if (event.isAltDown()) {
            sb.append(", alt");
        }
        if (event.isMetaDown()) {
            sb.append(", meta");
        }
        if (event.isShortcutDown()) {
            sb.append(", shortcut");
        }

        System.out.println(sb);
    }

    private boolean isShift() {
        return (eventModifierTypes.contains(EventModifierType.SHIFT_LEFT) ||
                eventModifierTypes.contains(EventModifierType.SHIFT_RIGHT));
    }

    private boolean isAltGR() {
        return eventModifierTypes.contains(EventModifierType.ALT_GR);
    }

    public KeyModifier keyModifier() {
        KeyModifier mode = KeyModifier.NORMAL;
        if (isShift() && isAltGR())
            mode = KeyModifier.SHIFT_ALT_GR;
        else if (isAltGR())
            mode = KeyModifier.ALT_GR;
        else if (isShift())
            mode = KeyModifier.SHIFT;

        return mode;
    }

    private static enum EventModifierType {
        SHIFT_LEFT, SHIFT_RIGHT, CTRL_LEFT, CTRL_RIGHT, ALT_LEFT, ALT_GR
    }
}