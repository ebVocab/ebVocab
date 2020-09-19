package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.config.VocabEnvironment;
import de.ebuchner.vocab.config.preferences.BooleanValue;
import de.ebuchner.vocab.config.preferences.IntegerValue;
import de.ebuchner.vocab.config.preferences.PreferenceValueList;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.VocabBaseController;
import de.ebuchner.vocab.model.core.ModelCommandManagerClearedEvent;
import de.ebuchner.vocab.model.font.FontModel;
import de.ebuchner.vocab.model.font.FontModelListener;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.NuiWindowParameter;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.nui.AbstractNuiWindow;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
todo can window and window controller be separated
http://stackoverflow.com/questions/13727314/prevent-or-cancel-exit-javafx-2
 */
public abstract class FxBaseWindow extends AbstractNuiWindow {

    private static final String PREF_LOCATION_X = "Location_X";
    private static final String PREF_LOCATION_Y = "Location_Y";
    private static final String PREF_ALWAYS_ON_TOP = "Always_On_Top";
    protected WindowClosingController windowClosingController = new WindowClosingController();
    protected FontChangeController fontChangeController = new FontChangeController();

    protected I18NContext i18n = I18NLocator.locate();
    protected Stage stage;
    private boolean alwaysOnTop;

    protected Config config() {
        return Config.instance();
    }

    private String generateWindowTitle(String resKey) {
        return String.format("%s - %s", i18n.getString(resKey), VocabEnvironment.APP_TITLE);
    }

    public final void nuiWindowCreate() {
        stage = new Stage();
        if (isVocabWindowModal())
            stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(config().appIconResourceName()));

        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setResources(i18n.getResources());
            loader.setController(this);
            Parent root = loader.load(loadWindowFXML());
            stage.setTitle(generateWindowTitle(vocabWindowTitleKey()));
            stage.setScene(createScene(root));
            onStageCreated();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        stage.setOnCloseRequest(windowClosingController);

        fontChangeController.fontChanged();
        FontModel.getOrCreateFontModel().addListener(fontChangeController);
    }

    private Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        String vocabStylesheetResource = String.format("%s.css", vocabWindowResourcePrefix());
        URL vocabStylesheetURL = getClass().getResource(vocabStylesheetResource);
        if (vocabStylesheetURL != null) {
            scene.getStylesheets().add(vocabStylesheetURL.toExternalForm());
            System.out.println("Loaded CSS from " + vocabStylesheetURL.toExternalForm());
        }
        return scene;
    }

    private InputStream loadWindowFXML() throws IOException {
        String vocabWindowResource = String.format("%s.fxml", vocabWindowResourcePrefix());
        return getClass().getResource(vocabWindowResource).openStream();
    }

    protected void onStageCreated() {

    }

    protected abstract String vocabWindowTitleKey();

    protected abstract String vocabWindowResourcePrefix();

    public final void nuiWindowShow(NuiWindowParameter parameter) {
        stage.sizeToScene();
        stage.setResizable(isVocabWindowResizable());
        restoreWindowAlwaysOnTop();
        restoreWindowLocation();
        if (parameter != null)
            nuiWindowReceive(parameter);

        if (!canHandleWindowParameter(parameter))
            return;

        onBeforeShow();

        showStage();
    }

    protected boolean canHandleWindowParameter(NuiWindowParameter parameter) {
        return true;
    }

    protected boolean isVocabWindowResizable() {
        return true;
    }

    protected boolean isVocabWindowModal() {
        return false;
    }

    protected void onBeforeShow() {

    }

    public final boolean attemptClosing() {
        return windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    // window is notified that closing is requested but may deny the request e.g. if a user's work
    // is not saved yet
    protected abstract NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType);

    // window is notified that closing is done and should store model data in VocabModel
    protected abstract void onVocabWindowClosed(NuiCloseEvent.CloseType closeType);

    private void saveWindowLocation() {
        if (!Config.projectInitialized())
            return;

        PreferenceValueList preferences = config().preferences().getPreferenceValueList();

        // Minimized Windows
        if (stage.isIconified())
            return;

        IntegerValue xPos = new IntegerValue(Double.valueOf(stage.getX()).intValue());

        preferences.putName(
                getClass(), PREF_LOCATION_X, xPos
        );

        IntegerValue yPos = new IntegerValue(Double.valueOf(stage.getY()).intValue());

        preferences.putName(
                getClass(), PREF_LOCATION_Y, yPos
        );
    }

    private void restoreWindowLocation() {
        if (!Config.projectInitialized())
            return;

        PreferenceValueList preferences = config().preferences().getPreferenceValueList();
        IntegerValue xPos = (IntegerValue) preferences.getName(getClass(), PREF_LOCATION_X);
        IntegerValue yPos = (IntegerValue) preferences.getName(getClass(), PREF_LOCATION_Y);

        if (xPos != null && yPos != null) {
            stage.setX(xPos.getValue());
            stage.setY(yPos.getValue());
        }
    }

    private void saveAlwaysOnTop() {
        if (!Config.projectInitialized())
            return;

        PreferenceValueList preferences = config().preferences().getPreferenceValueList();

        BooleanValue alwaysOnTopValue = new BooleanValue(alwaysOnTop);

        preferences.putName(
                getClass(), PREF_ALWAYS_ON_TOP, alwaysOnTopValue
        );
    }

    private void restoreWindowAlwaysOnTop() {
        if (!Config.projectInitialized())
            return;

        BooleanValue value = (BooleanValue) config().preferences().getPreferenceValueList().getName(
                getClass(), PREF_ALWAYS_ON_TOP
        );
        if (value == null)
            alwaysOnTop = false;
        else
            alwaysOnTop = value.getValue();
        stage.setAlwaysOnTop(alwaysOnTop);
    }

    protected final boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    protected final void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
        stage.setAlwaysOnTop(alwaysOnTop);
    }

    protected Map<WindowType.MenuType, List<WindowType>> menuTypeMap() {
        Map<WindowType.MenuType, List<WindowType>> menuTypeMap = new HashMap<>();
        for (WindowType.MenuType menuType : WindowType.MenuType.values()) {
            menuTypeMap.put(
                    menuType,
                    WindowType.getAvailableWindowTypes(menuType, windowTypeFilter())
            );
        }
        return menuTypeMap;
    }

    protected List<MenuItem> createWindowTypeMenuItems(
            Menu menu,
            WindowType windowType,
            VocabBaseController practiceController
    ) {
        List<MenuItem> items = new ArrayList<>();

        for (NuiWindowParameter parameter : windowType.nuiWindowParameters()) {
            final NuiWindowParameter myParameter = parameter;
            String res = windowTypeRes(windowType, parameter);

            MenuItem menuItem = new MenuItem();
            menuItem.setText(i18n.getString(res));
            menuItem.setAccelerator(acceleratorRes(res));
            menuItem.setOnAction(event -> practiceController.onOpenWindowType(windowType, myParameter));
            menuItem.setGraphic(iconRes(res));
            items.add(menuItem);
        }
        return items;
    }

    protected Node iconRes(String res) {
        String iconRes = i18n.getOptionalString(String.format("%s.icon", res), null);
        if (iconRes == null)
            return null;

        return new ImageView(
                new Image(iconRes)
        );
    }

    private KeyCombination acceleratorRes(String res) {
        String acceleratorString = i18n.getOptionalString(String.format("%s.accelerator", res), null);
        if (acceleratorString == null)
            return null;

        return KeyCombination.valueOf(acceleratorString);
    }

    private String windowTypeRes(WindowType windowType, NuiWindowParameter parameter) {
        String parameterToken = parameter.getToken();
        if (parameterToken != null)
            return String.format("nui.menu.tools.%s.%s", windowType.getToken(), parameter.getToken());

        return String.format("nui.menu.tools.%s", windowType.getToken());
    }

    protected void createMenuItems(Menu menu, WindowType.MenuType menuType, VocabBaseController controller) {
        int addPos = 0;
        Map<WindowType.MenuType, List<WindowType>> menuWindowTypes = menuTypeMap();
        for (WindowType windowType : menuWindowTypes.get(menuType)) {
            List<MenuItem> items = createWindowTypeMenuItems(menu, windowType, controller);
            for (MenuItem item : items) {
                menu.getItems().add(addPos, item);
                addPos++;
            }
        }
    }

    protected void createMenuItem(Menu menu, Class<? extends UITool> uiToolClass) {
        UITool uiTool;
        try {
            uiTool = uiToolClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String res = uiTool.getResourceKey();
        MenuItem menuItem = new MenuItem();
        menuItem.setText(i18n.getString(res));
        menuItem.setAccelerator(acceleratorRes(res));
        menuItem.setGraphic(iconRes(res));
        final UITool finalUiTool = uiTool;
        menuItem.setOnAction(event -> {
                    try {
                        finalUiTool.onOpenTool();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        menu.getItems().add(menuItem);

        uiTool.onToolCreated(
                FxUIPlatform.instance().getNuiDirector().getLastTextFieldWithFocus(),
                menuItem
        );
        addEventListener(uiTool);
    }

    public void createStandardToolbar(
            ToolBar toolBar,
            VocabBaseController controller
    ) {
        createStandardToolbar(
                toolBar,
                controller,
                toolBar.getItems().size()
        );
    }

    public void createStandardToolbar(
            ToolBar toolBar,
            VocabBaseController controller,
            int addPos
    ) {

        int entriesCreated = 0;

        Map<WindowType.MenuType, List<WindowType>> menuWindowTypes = menuTypeMap();
        for (WindowType windowType : menuWindowTypes.get(WindowType.MenuType.FILE)) {
            for (Button button : createWindowTypeButtons(windowType, controller)) {
                toolBar.getItems().add(addPos, button);
                addPos++;
                entriesCreated++;
            }
        }

        if (entriesCreated > 0) {
            entriesCreated = 0;
            toolBar.getItems().add(addPos, new Separator());
            addPos++;
        }

        for (WindowType windowType : menuWindowTypes.get(WindowType.MenuType.SEARCH)) {
            for (Button button : createWindowTypeButtons(windowType, controller)) {
                toolBar.getItems().add(addPos, button);
                addPos++;
                entriesCreated++;
            }
        }

        if (entriesCreated > 0) {
            entriesCreated = 0;
            toolBar.getItems().add(addPos, new Separator());
            addPos++;
        }

        /* WindowType.MenuType.Tools: tools are not included in toolbar */

        for (WindowType windowType : menuWindowTypes.get(WindowType.MenuType.TEXT)) {
            for (Button button : createWindowTypeButtons(windowType, controller)) {
                toolBar.getItems().add(addPos, button);
                addPos++;
                entriesCreated++;
            }
        }

        // Belongs to text
        toolBar.getItems().add(addPos, createToolButton(TranslateTool.class));
        addPos++;
        entriesCreated++;

        if (entriesCreated > 0) {
            entriesCreated = 0;
            toolBar.getItems().add(addPos, new Separator());
            addPos++;
        }

        for (WindowType windowType : menuWindowTypes.get(WindowType.MenuType.OPTIONS)) {
            for (Button button : createWindowTypeButtons(windowType, controller)) {
                toolBar.getItems().add(addPos, button);
                addPos++;
                entriesCreated++;
            }
        }

    }

    private Button createToolButton(Class<? extends UITool> uiToolClass) {
        UITool uiTool;
        try {
            uiTool = uiToolClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String res = uiTool.getResourceKey();
        Button button = new Button();
        button.setTooltip(createTooltip(i18n.getString(res)));
        button.setGraphic(iconRes(res));
        button.setOnAction(event -> {
                    try {
                        uiTool.onOpenTool();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        uiTool.onToolCreated(
                FxUIPlatform.instance().getNuiDirector().getLastTextFieldWithFocus(),
                button
        );
        addEventListener(uiTool);
        return button;
    }

    private Iterable<Button> createWindowTypeButtons(WindowType windowType, VocabBaseController controller) {
        List<Button> buttons = new ArrayList<>();
        for (NuiWindowParameter parameter : windowType.nuiWindowParameters()) {
            String res = windowTypeRes(windowType, parameter);
            String text = i18n.getOptionalString(res, null);

            Button button = new Button();
            if (text != null)
                button.setTooltip(createTooltip(text));
            button.setOnAction(event -> controller.onOpenWindowType(windowType, parameter));
            button.setGraphic(iconRes(res));
            buttons.add(button);
        }
        return buttons;
    }

    protected Tooltip createTooltip(String rawText) {
        StringBuilder text = new StringBuilder();
        for (char c : rawText.toCharArray()) {
            if ('_' != c)
                text.append(c);
        }
        return new Tooltip(text.toString());
    }

    protected final NuiClosingResult confirmCloseTypeCancel() {
        FxDialogs.Action response = FxDialogs.create()
                .title(i18n.getString(vocabWindowTitleKey()))
                .message(i18n.getString("nui.confirm.close"))
                .actions(FxDialogs.ActionType.YES, FxDialogs.ActionType.CANCEL)
                .showConfirm();

        if (response.getActionType() == FxDialogs.ActionType.YES)
            return NuiClosingResult.CLOSING_OK;

        return NuiClosingResult.CLOSING_NOT_ALLOWED;
    }

    protected void createTextFieldContextMenu(
            VocabBaseController controller,
            TextInputControl textField,
            List<Class<? extends UITool>> uiToolClasses
    ) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuWindowParameter parameter =
                new ContextMenuWindowParameter(textField);

        WindowType.getAvailableWindowTypes(windowTypeFilter())
                .stream()
                .filter(
                        windowType -> windowType.getTextFieldContextMenu() == WindowType.TextFieldContextMenu.ON
                ).forEach(windowType -> {
            MenuItem menuItem = new MenuItem();
            String res = windowTypeRes(windowType, parameter);
            menuItem.setText(i18n.getString(res));
            menuItem.setGraphic(iconRes(res));
            menuItem.setOnAction(event -> controller.onOpenWindowType(windowType, parameter));
            contextMenu.getItems().add(
                    menuItem
            );
        });

        if (!contextMenu.getItems().isEmpty() && !uiToolClasses.isEmpty())
            contextMenu.getItems().add(new SeparatorMenuItem());

        for (Class<? extends UITool> uiToolClass : uiToolClasses) {
            UITool uiTool;
            try {
                uiTool = uiToolClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            MenuItem menuItem = new MenuItem();
            String res = uiTool.getResourceKey();
            menuItem.setText(i18n.getString(res));
            menuItem.setAccelerator(acceleratorRes(res));
            menuItem.setGraphic(iconRes(res));

            menuItem.setOnAction(event -> {
                try {
                    uiTool.onContextMenuClick(textField);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            contextMenu.getItems().add(
                    menuItem
            );
        }

        textField.setContextMenu(contextMenu);
    }

    public String uiTitle(String resKey, String extra) {
        return String.format("%s - %s - %s", i18n.getString(resKey), extra, VocabEnvironment.APP_TITLE);
    }

    public String uiTitleNoResKey(String title) {
        return String.format("%s - %s", title, VocabEnvironment.APP_TITLE);
    }

    private void showStage() {
        if (isVocabWindowModal())
            stage.showAndWait();
        else
            stage.show();
    }

    protected class WindowClosingController implements EventHandler<WindowEvent> {
        public boolean doWindowClosing(NuiCloseEvent.CloseType closeType) {
            // prepare the window itself for closing
            if (onVocabWindowClosing(closeType) == NuiClosingResult.CLOSING_NOT_ALLOWED)
                return false;

            // prepare the listeners for closing
            if (fireOnNuiWindowClosing(closeType) == NuiClosingResult.CLOSING_NOT_ALLOWED)
                return false;

            // let the window update vocab model
            onVocabWindowClosed(closeType);

            saveAlwaysOnTop();
            saveWindowLocation();
            stage.close();
            // this will remove the instance from NuiDirector and will create a new instance next time
            fireOnNuiWindowClosed(closeType);
            return true;
        }

        public void handle(WindowEvent event) {
            if (!doWindowClosing(NuiCloseEvent.CloseType.CLOSED)) {
                event.consume();
                showStage();
            }
        }
    }

    private class FontChangeController implements FontModelListener {

        @Override
        public void fontChanged() {
            FxFontModifier.modifyFontInUI(config().extraFontHeight(), stage.getScene().getRoot());

            stage.sizeToScene();
        }

        @Override
        public void modelCommandManagerCleared(ModelCommandManagerClearedEvent event) {

        }
    }


}
