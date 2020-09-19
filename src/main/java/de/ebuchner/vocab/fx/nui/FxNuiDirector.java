package de.ebuchner.vocab.fx.nui;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.cloud.CloudModel;
import de.ebuchner.vocab.model.editor.EditorModel;
import de.ebuchner.vocab.model.io.FieldDecoder;
import de.ebuchner.vocab.model.io.FieldEncoder;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import de.ebuchner.vocab.model.nui.*;
import de.ebuchner.vocab.model.nui.focus.FocusAware;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import de.ebuchner.vocab.nui.NuiDirector;
import de.ebuchner.vocab.nui.NuiDirectorListener;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FxNuiDirector extends NuiDirector {

    // must only be created once. Creating the second instance results in IllegalArgumentException
    private static final DataFormat VOCAB_DATA_FORMAT = new DataFormat("text/vocab");

    protected List<NuiWindow> windowInstances = new ArrayList<>();
    ShutDownSequence shutDownSequence = ShutDownSequence.INIT;
    private NuiEventManager eventManager = new NuiEventManager();

    private Map<WindowType, NuiWindow> mockWindows = new HashMap<>();
    private List<NuiDirectorListener> listeners = new ArrayList<>();
    private DefaultNuiDirectorListener nuiDirectorListener = new DefaultNuiDirectorListener();

    public FxNuiDirector() {
        addNuiDirectorListener(nuiDirectorListener);
    }

    // test only
    boolean isCreated(WindowType windowType) {
        if (windowType == null)
            throw new IllegalArgumentException("Window type must not be null");
        return findWindow(windowType) != null;
    }

    // test only
    private NuiWindow findWindow(WindowType windowType) {
        for (NuiWindow window : windowInstances) {
            if (window.windowType().equals(windowType))
                return window;
        }

        return null;
    }

    @Override
    public NuiWindow showWindow(WindowType windowType) {
        if (windowType == null)
            throw new IllegalArgumentException("Window type must not be null");

        if (windowType.getMaxInstances() != WindowType.MaxInstances.MULTIPLE) {
            NuiWindow nuiWindow = findWindow(windowType);
            if (nuiWindow != null) {
                nuiWindow.nuiWindowShow(NuiWindowParameter.EMPTY);
                return nuiWindow;
            }
        }

        return super.showWindow(windowType);
    }

    @Override
    protected NuiWindow createWindow(WindowType windowType, NuiWindowParameter windowParameter) {
        NuiWindow nuiWindow;

        if (mockWindows.containsKey(windowType))
            nuiWindow = mockWindows.get(windowType);
        else {
            try {
                String className = UIPlatformFactory.getUIPlatform().windowClassName(windowType.getWindowClassName());
                nuiWindow = (NuiWindow) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!nuiWindow.canCreate(windowParameter))
            return null;
        nuiWindow.addEventListener(eventManager);
        nuiWindow.nuiWindowCreate();

        windowInstances.add(nuiWindow);
        nuiWindow.onWindowInstanceRegistered();
        System.out.println("NUI Window created: " + nuiWindow.windowType().getToken());

        return nuiWindow;
    }

    // test only
    public void putMockWindow(WindowType windowType, NuiWindow mockWindow) {
        mockWindows.put(windowType, mockWindow);
    }

    // test only
    public void removeMockWindow(WindowType windowType) {
        NuiWindow window = mockWindows.remove(windowType);
        windowInstances.remove(window);
    }

    public boolean closeAll() {
        nuiDirectorListener.setDisabled(true);
        try {
            List<NuiWindow> copy = new ArrayList<>();
            copy.addAll(windowInstances);
            for (NuiWindow window : copy) {
                if (!window.attemptClosing())
                    return false;

            }
            return true;
        } finally {
            nuiDirectorListener.setDisabled(false);
        }
    }

    @Override
    public boolean entriesToClipboard(List<VocabEntry> entriesToCopy) {
        ClipboardContent entriesContent = new ClipboardContent();
        String entriesAsString = new FieldEncoder().encodeToString(entriesToCopy);
        entriesContent.put(VOCAB_DATA_FORMAT, entriesAsString);
        entriesContent.put(DataFormat.PLAIN_TEXT, entriesAsString);
        Clipboard.getSystemClipboard().setContent(entriesContent);

        return true;
    }

    public List<VocabEntry> entriesFromClipboard() {
        String entriesAsString = (String) Clipboard.getSystemClipboard().getContent(VOCAB_DATA_FORMAT);
        if (entriesAsString == null)
            return null;

        VocabEntryList entryList = new FieldDecoder().decodeFromString(entriesAsString);
        if (entryList == null)
            return null;

        return entryList.toListOfEntries();
    }

    public void addNuiDirectorListener(NuiDirectorListener listener) {
        listeners.add(listener);
    }

    public void removeNuiDirectorListener(NuiDirectorListener listener) {
        listeners.remove(listener);
    }

    @Override
    public NuiTextFieldWithFocus getLastTextFieldWithFocus() {
        return eventManager.textFieldWithFocus;
    }

    @Override
    public void sendWindow(NuiWindowParameter windowParameter) {
        for (NuiWindow nuiWindow : windowInstances) {
            nuiWindow.nuiWindowReceive(windowParameter);
        }
    }

    @Override
    protected void startUpSystemDependent() {
        Platform.setImplicitExit(false);
    }

    @Override
    protected void exitSystemDependant() {
        Platform.exit();
    }

    enum ShutDownSequence {
        INIT, CLOUD
    }

    class NuiEventManager implements NuiEventListener {
        NuiTextFieldWithFocus textFieldWithFocus;

        public NuiClosingResult onNuiWindowClosing(NuiCloseEvent event) {
            return NuiClosingResult.CLOSING_OK;
        }

        public void onNuiWindowClosed(NuiCloseEvent event) {
            windowInstances.remove(event.getSource());
            event.getSource().removeEventListener(this); //ConcurrentModificationException
            System.out.println("NUI Window closed: " + event.getSource().windowType().getToken());

            if (windowInstances.isEmpty()) {
                for (NuiDirectorListener listener : listeners) {
                    listener.lastWindowsWasClosed();
                }
            }
        }

        public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {
            this.textFieldWithFocus = textFieldWithFocus;
            for (NuiWindow window : windowInstances) {
                if (window instanceof FocusAware) {
                    FocusAware focusAware = (FocusAware) window;
                    focusAware.onFocusChangedTo(textFieldWithFocus);
                }
            }
        }

        public void onFocusLost() {
            for (NuiWindow window : windowInstances) {
                if (window instanceof FocusAware) {
                    FocusAware focusAware = (FocusAware) window;
                    focusAware.onFocusLost();
                }
            }
            textFieldWithFocus = null;
        }
    }

    private class DefaultNuiDirectorListener implements NuiDirectorListener {
        private boolean disabled;

        public void lastWindowsWasClosed() {
            if (disabled)
                return;

            boolean cloudWindowOpened = false;
            if (openCloudWindowOnExit()) {
                cloudWindowOpened = true;
                showWindow(WindowType.CLOUD_WINDOW);
            }

            if (!cloudWindowOpened)
                shutDownImpl(true);
        }

        private boolean openCloudWindowOnExit() {
            shutDownSequence = ShutDownSequence.CLOUD;

            if (!Config.projectInitialized())
                return false;

            if (!WindowType.CLOUD_WINDOW.isAvailable())
                return false;

            CloudModel cloudModel = CloudModel.getOrCreateCloudModel();
            long lastSaved = EditorModel.getOrCreateEditorModel().getLastSaved();
            long lastUpload = cloudModel.getLastUpload();
            if (lastSaved > lastUpload) {
                I18NContext i18n = I18NLocator.locate();
                FxDialogs.Action action = FxDialogs.create()
                        .actions(FxDialogs.ActionType.YES, FxDialogs.ActionType.NO)
                        .message(i18n.getString("nui.cloud.hint"))
                        .showConfirm();

                return action.getActionType() == FxDialogs.ActionType.YES;
            }
            return false;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }

}
