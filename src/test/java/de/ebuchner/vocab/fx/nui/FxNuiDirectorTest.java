package de.ebuchner.vocab.fx.nui;

import de.ebuchner.vocab.config.ProjectConfig;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.NuiEventListener;
import de.ebuchner.vocab.model.nui.focus.NuiTextFieldWithFocus;
import de.ebuchner.vocab.nui.MyNuiWindow;
import de.ebuchner.vocab.nui.NuiStarter;
import junit.framework.TestCase;

import java.util.List;

public class FxNuiDirectorTest extends TestCase {

    @Override
    protected void setUp() {
        new NuiStarter().prepareProjectDir();
    }

    public void testDirector() {
        DesktopNuiDirectorForTest director = new DesktopNuiDirectorForTest();

        try {
            director.isCreated(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            director.showWindow(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        assertFalse(director.isCreated(MyNuiWindow.WINDOW_ID));

        // create instance from ID
        MyNuiWindow myNuiWindow = (MyNuiWindow) director.showWindow(MyNuiWindow.WINDOW_ID);
        assertTrue(director.isCreated(MyNuiWindow.WINDOW_ID));
        assertNotNull(myNuiWindow);
        assertEquals(1, myNuiWindow.getCreateCount());

        // try closing instance
        assertEquals(NuiClosingResult.CLOSING_OK, myNuiWindow.tryClose());
        NaySayerListener listener = new NaySayerListener();
        myNuiWindow.addEventListener(listener);
        assertEquals(NuiClosingResult.CLOSING_NOT_ALLOWED, myNuiWindow.tryClose());
        myNuiWindow.removeEventListener(listener);
        assertEquals(NuiClosingResult.CLOSING_OK, myNuiWindow.tryClose());

        assertTrue(director.isCreated(MyNuiWindow.WINDOW_ID));

        int oldSize = myNuiWindow.getListeners().size();
        myNuiWindow.addEventListener(new SelfRemovingListener());
        assertEquals(oldSize + 1, myNuiWindow.getListeners().size());

        // close instance
        myNuiWindow.doClose();
        assertFalse(director.isCreated(MyNuiWindow.WINDOW_ID));
        assertEquals(0, myNuiWindow.getListeners().size());

        // reopen
        MyNuiWindow myNuiWindow2 = (MyNuiWindow) director.showWindow(MyNuiWindow.WINDOW_ID);
        assertEquals(1, myNuiWindow2.getCreateCount());
        assertFalse(myNuiWindow == myNuiWindow2);
        myNuiWindow2.doClose();
        assertTrue(director.systemExit);
    }

    static class DesktopNuiDirectorForTest extends FxNuiDirector {
        boolean systemExit = false;

        @Override
        protected void exitSystemDependant() {
            systemExit = true;
        }

        @Override
        public boolean entriesToClipboard(List<VocabEntry> entriesToCopy) {
            throw new UnsupportedOperationException();
        }
    }

    class NaySayerListener implements NuiEventListener {
        public void onNuiWindowClosed(NuiCloseEvent event) {

        }

        public NuiClosingResult onNuiWindowClosing(NuiCloseEvent event) {
            return NuiClosingResult.CLOSING_NOT_ALLOWED;
        }

        public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {

        }

        public void onFocusLost() {

        }
    }

    class SelfRemovingListener implements NuiEventListener {
        public void onNuiWindowClosed(NuiCloseEvent event) {
            MyNuiWindow myNuiWindow = (MyNuiWindow) event.getSource();
            myNuiWindow.removeEventListener(this);
            myNuiWindow.assertNotListener(this);
        }

        public NuiClosingResult onNuiWindowClosing(NuiCloseEvent event) {
            return NuiClosingResult.CLOSING_OK;
        }

        public void onFocusChangedTo(NuiTextFieldWithFocus textFieldWithFocus) {

        }

        public void onFocusLost() {

        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        ProjectConfig.instance().dispose();
    }
}
