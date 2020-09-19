package de.ebuchner.vocab.nui;

import de.ebuchner.vocab.model.nui.*;

import java.util.List;

public class MyNuiWindow extends AbstractNuiWindow {
    public static final WindowType WINDOW_ID = new WindowType("de.ebuchner.vocab.nui.MyNuiWindow");

    int createCount = 0;

    public WindowType windowType() {
        return WINDOW_ID;
    }

    public void nuiWindowCreate() {
        createCount = createCount + 1;
    }

    public void nuiWindowShow(NuiWindowParameter parameter) {
    }

    public boolean attemptClosing() {
        return true;
    }

    public NuiClosingResult tryClose() {
        return fireOnNuiWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    public void doClose() {
        fireOnNuiWindowClosed(NuiCloseEvent.CloseType.CLOSED);
    }

    public void assertNotListener(NuiEventListener listener) {
        if (listeners.contains(listener))
            throw new RuntimeException("Assertion failed");
    }

    public int getCreateCount() {
        return createCount;
    }

    public List<NuiEventListener> getListeners() {
        return listeners;
    }
}
