package de.ebuchner.vocab.fx.practice;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.config.fields.Field;
import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.fx.common.*;
import de.ebuchner.vocab.fx.focus.FxTextFieldWithFocus;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.lessons.RepetitionMode;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.practice.*;
import de.ebuchner.vocab.nui.NuiDirector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import java.io.File;
import java.util.*;

public class PracticeWindow extends FxBaseWindow {

    @FXML
    private Button btnRepetitionLoad;
    @FXML
    private Menu menuEdit;
    @FXML
    private MenuItem miNavigationReset;
    @FXML
    private MenuItem miNavigationBackward;
    @FXML
    private MenuItem miNavigationForward;
    @FXML
    private MenuItem miReshuffle;
    @FXML
    private MenuItem miRepetitionAdd;
    @FXML
    private MenuItem miRepetitionRemove;
    @FXML
    private MenuItem miRepetitionClear;
    @FXML
    private CheckBox cbLessonShowOption;
    @FXML
    private Label lbLesson;
    @FXML
    private TextField tfLesson;
    @FXML
    private Label lbForeign;
    @FXML
    private TextField tfForeign;
    @FXML
    private Label lbType;
    @FXML
    private TextField tfType;
    @FXML
    private Label lbUser;
    @FXML
    private TextField tfUser;
    @FXML
    private Label lbComment;
    @FXML
    private TextArea tfComment;
    @FXML
    private Button btnEditEntry;
    @FXML
    private Button btnNavigationReset;
    @FXML
    private Button btnNavigationBackward;
    @FXML
    private Button btnNavigationForward;
    @FXML
    private Button btnReshuffle;
    @FXML
    private CheckBox cbRepetitionMode;
    @FXML
    private Button btnRepetitionAdd;
    @FXML
    private Button btnRepetitionRemove;
    @FXML
    private Button btnRepetitionClear;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuTools;
    @FXML
    private Menu menuText;
    @FXML
    private Menu menuOptions;
    @FXML
    private Menu menuHelp;
    @FXML
    private CheckMenuItem miShowStatistics;
    @FXML
    private CheckMenuItem miItemReverse;
    @FXML
    private RadioMenuItem miStrategyRandom;
    @FXML
    private RadioMenuItem miStrategyIntense;
    @FXML
    private RadioMenuItem miStrategyBrowse;
    @FXML
    private RadioMenuItem miStrategyFrequency;
    @FXML
    private RadioMenuItem miStrategyAge;
    @FXML
    private Label statusBar;
    @FXML
    private ToolBar toolBar;
    @FXML
    private ProgressBar pbVocab;
    @FXML
    private Menu menuSearch;
    @FXML
    private Label lbStrategy;

    private DesktopPracticeController practiceController;

    public WindowType windowType() {
        return WindowType.PRACTICE_WINDOW;
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {

    }

    @Override
    protected void onStageCreated() {
        practiceController = new DesktopPracticeController(new MyBehaviour());
        NuiDirector nuiDirector = FxUIPlatform.instance().getNuiDirector();

        lbForeign.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.FOREIGN)));
        lbType.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.TYPE)));
        lbUser.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.USER)));
        lbComment.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.COMMENT)));

        createMenuItems(menuFile, WindowType.MenuType.FILE, practiceController);
        createMenuItems(menuSearch, WindowType.MenuType.SEARCH, practiceController);
        createMenuItems(menuText, WindowType.MenuType.TEXT, practiceController);
        createMenuItem(menuText, TranslateTool.class);
        createMenuItems(menuTools, WindowType.MenuType.TOOLS, practiceController);
        createMenuItems(menuOptions, WindowType.MenuType.OPTIONS, practiceController);

        createMenuItems(menuHelp, WindowType.MenuType.HELP, practiceController);

        createStandardToolbar(toolBar, practiceController, 0);

        // if buttons are hidden, request a layout refresh to free empty space
        // http://stackoverflow.com/questions/12200195/javafx-hbox-hide-item
        btnRepetitionAdd.managedProperty().bind(btnRepetitionAdd.visibleProperty());
        btnRepetitionRemove.managedProperty().bind(btnRepetitionRemove.visibleProperty());
        btnRepetitionClear.managedProperty().bind(btnRepetitionClear.visibleProperty());

        btnNavigationForward.setDefaultButton(true);

        List<Class<? extends UITool>> uiToolClassList = Collections.singletonList(TranslateTool.class);
        createTextFieldContextMenu(practiceController, tfForeign, uiToolClassList);
        createTextFieldContextMenu(practiceController, tfUser, uiToolClassList);
        createTextFieldContextMenu(practiceController, tfComment, uiToolClassList);

        pbVocab.setProgress(0.0);

        // accelerators
        miRepetitionAdd.setAccelerator(new KeyCodeCombination(KeyCode.PLUS));
        miRepetitionRemove.setAccelerator(new KeyCodeCombination(KeyCode.MINUS));

        Platform.runLater(() -> practiceController.onWindowWasCreated());
    }

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.practice.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "practice";
    }

    public void onLessonShowOptionChanged(ActionEvent actionEvent) {
        practiceController.onLessonShowOptionChanged(cbLessonShowOption.isSelected());
    }

    public void onEditEntry(ActionEvent actionEvent) {
        practiceController.onEditEntry();
    }

    public void onNavigationReset(ActionEvent actionEvent) {
        practiceController.onNavigationReset();
    }

    public void onNavigationBackward(ActionEvent actionEvent) {
        practiceController.onNavigationBackward();
    }

    public void onNavigationForward(ActionEvent actionEvent) {
        practiceController.onNavigationForward();
    }

    public void onNavigationReshuffle(ActionEvent actionEvent) {
        practiceController.onNavigationReshuffle();
    }

    public void onRepetitionActivated(ActionEvent actionEvent) {
        practiceController.onRepetitionActivated(cbRepetitionMode.isSelected());
    }

    public void onRepetitionAdd(ActionEvent actionEvent) {
        practiceController.onRepetitionAdd();
    }

    public void onRepetitionRemove(ActionEvent actionEvent) {
        practiceController.onRepetitionRemove();
    }

    public void onRepetitionClear(ActionEvent actionEvent) {
        FxDialogs.Action action = FxDialogs.create()
                .title(i18n.getString("nui.practice.auto.save.title"))
                .message(i18n.getString("nui.practice.auto.save.message"))
                .actions(FxDialogs.ActionType.YES)
                .actions(FxDialogs.ActionType.NO)
                .actions(FxDialogs.ActionType.CANCEL)
                .showConfirm();

        if (action.getActionType() == FxDialogs.ActionType.YES)
            practiceController.onRepetitionClear(true);
        else if (action.getActionType() == FxDialogs.ActionType.NO)
            practiceController.onRepetitionClear(false);
    }

    public void onFileExit(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    public void onOptionsShowStatistics(ActionEvent actionEvent) {
        practiceController.onShowStatisticsOptionChanged(
                miShowStatistics.isSelected()
        );
    }

    public void onOptionsItemReverse(ActionEvent actionEvent) {
        practiceController.onReverseOptionChanged(miItemReverse.isSelected());
    }

    public void onStrategyChanged(ActionEvent actionEvent) {
        SelectedStrategy selectedStrategy;
        if (miStrategyAge.isSelected())
            selectedStrategy = SelectedStrategy.AGE;
        else if (miStrategyBrowse.isSelected())
            selectedStrategy = SelectedStrategy.BROWSE;
        else if (miStrategyFrequency.isSelected())
            selectedStrategy = SelectedStrategy.FREQUENCY;
        else if (miStrategyIntense.isSelected())
            selectedStrategy = SelectedStrategy.INTENSE;
        else if (miStrategyRandom.isSelected())
            selectedStrategy = SelectedStrategy.RANDOM;
        else
            throw new RuntimeException("Unknown strategy option selected");
        practiceController.onStrategyChanged(selectedStrategy);
    }

    public void onRepetitionLoad(ActionEvent actionEvent) {
        practiceController.onRepetitionLoad();
    }

    class MyBehaviour implements PracticeWindowBehaviour {

        Map<String, FieldRenderer> rendererCache = new HashMap<>();
        private final FieldRenderer lessonFieldRenderer = new DefaultFieldRenderer();

        public void setStrategyOptionsEnabled(boolean enabled) {
            miStrategyRandom.setDisable(!enabled);
            miStrategyIntense.setDisable(!enabled);
            miStrategyBrowse.setDisable(!enabled);
            miStrategyAge.setDisable(!enabled);
            miStrategyFrequency.setDisable(!enabled);
            if (!enabled)
                lbStrategy.setText("");
        }

        public void setNavigationForwardEnabled(boolean enabled) {
            btnNavigationForward.setDisable(!enabled);
            miNavigationForward.setDisable(!enabled);
            btnReshuffle.setDisable(!enabled);
            miReshuffle.setDisable(!enabled);
        }

        public void setNavigationBackwardEnabled(boolean enabled) {
            btnNavigationBackward.setDisable(!enabled);
            miNavigationBackward.setDisable(!enabled);
        }

        private TextInputControl findTextField(Field field) {
            TextInputControl uiField = null;
            switch (field.name()) {
                case FieldFactory.FOREIGN:
                    uiField = tfForeign;
                    break;
                case FieldFactory.TYPE:
                    uiField = tfType;
                    break;
                case FieldFactory.USER:
                    uiField = tfUser;
                    break;
                case FieldFactory.COMMENT:
                    uiField = tfComment;
                    break;
            }
            return uiField;
        }

        private Label findLabel(Field field) {
            Label uiLabel = null;
            switch (field.name()) {
                case FieldFactory.FOREIGN:
                    uiLabel = lbForeign;
                    break;
                case FieldFactory.TYPE:
                    uiLabel = lbType;
                    break;
                case FieldFactory.USER:
                    uiLabel = lbUser;
                    break;
                case FieldFactory.COMMENT:
                    uiLabel = lbComment;
                    break;
            }
            return uiLabel;
        }

        public void setFieldEnabled(Field field, boolean enabled) {
            if (FieldFactory.FOREIGN.equals(field.name()))
                btnEditEntry.setDisable(!enabled);

            TextInputControl uiField = findTextField(field);
            if (uiField != null) {
                if (!enabled)
                    uiField.setText("");
                uiField.setDisable(!enabled);
            }

            Label uiLabel = findLabel(field);
            if (uiLabel != null)
                uiLabel.setDisable(!enabled);
        }

        public void setSelectedStrategy(SelectedStrategy selectedStrategy) {
            String reshuffleTooltip = null;
            String reshuffleText = null;
            String skipIcon = null;
            String strategyLabel = null;

            if (selectedStrategy == SelectedStrategy.BROWSE) {
                miStrategyBrowse.setSelected(true);
                skipIcon = "/de/ebuchner/vocab/nui/res/strategy_goto.png";
                reshuffleTooltip = "nui.practice.navigation.button.reshuffle.tooltip.browse";
                reshuffleText = "nui.practice.navigation.button.reshuffle.browse";
                strategyLabel = ".browse";
            } else if (selectedStrategy == SelectedStrategy.RANDOM) {
                miStrategyRandom.setSelected(true);
                skipIcon = "/de/ebuchner/vocab/nui/res/strategy_reshuffle.png";
                reshuffleTooltip = "nui.practice.navigation.button.reshuffle.tooltip.random";
                reshuffleText = "nui.practice.navigation.button.reshuffle.random";
                strategyLabel = ".random";
            } else if (selectedStrategy == SelectedStrategy.AGE) {
                miStrategyAge.setSelected(true);
                skipIcon = "/de/ebuchner/vocab/nui/res/strategy_reshuffle.png";
                reshuffleTooltip = "nui.practice.navigation.button.reshuffle.tooltip.statistics";
                reshuffleText = "nui.practice.navigation.button.reshuffle.statistics";
                strategyLabel = ".age";
            } else if (selectedStrategy == SelectedStrategy.FREQUENCY) {
                miStrategyFrequency.setSelected(true);
                skipIcon = "/de/ebuchner/vocab/nui/res/strategy_reshuffle.png";
                reshuffleTooltip = "nui.practice.navigation.button.reshuffle.tooltip.statistics";
                reshuffleText = "nui.practice.navigation.button.reshuffle.statistics";
                strategyLabel = ".frequency";
            } else if (selectedStrategy == SelectedStrategy.INTENSE) {
                miStrategyIntense.setSelected(true);
                skipIcon = "/de/ebuchner/vocab/nui/res/strategy_skip.png";
                reshuffleTooltip = "nui.practice.navigation.button.reshuffle.tooltip.intense";
                reshuffleText = "nui.practice.navigation.button.reshuffle.intense";
                strategyLabel = ".intense";
            }

            if (skipIcon != null) {
                btnReshuffle.setGraphic(new ImageView(new Image(skipIcon)));
                miReshuffle.setGraphic(new ImageView(new Image(skipIcon)));
            }

            if (reshuffleText != null) {
                btnReshuffle.setText(i18n.getString(reshuffleText));
                miReshuffle.setText(i18n.getString(reshuffleText));
            }

            if (reshuffleTooltip != null)
                btnReshuffle.setTooltip(createTooltip(i18n.getString(reshuffleTooltip)));

            if (strategyLabel != null)
                lbStrategy.setText(
                        i18n.getString(
                                "nui.practice.strategy.label",
                                Collections.singletonList(
                                        i18n.getString(
                                                String.format("nui.practice.strategy%s", strategyLabel)
                                        )
                                )
                        )
                );
        }

        public void setReverseOptionEnabled(boolean enabled) {
            miItemReverse.setDisable(!enabled);
        }

        public void setReverseOption(PracticeReverse reverseOption) {
            miItemReverse.setSelected(reverseOption == PracticeReverse.REVERSE);
        }

        @Override
        public void setImageOptionEnabled(boolean enabled) {

        }

        @Override
        public void setImageOption(PracticeImage imageOption) {

        }

        @Override
        public void setShowStatisticsOptionEnabled(boolean enabled) {
            miShowStatistics.setDisable(!enabled);
        }

        @Override
        public void setShowStatisticsOption(boolean showStatistics) {
            miShowStatistics.setSelected(showStatistics);
        }

        public void renderField(FieldRendererContext context) {
            FieldRenderer renderer = findRenderer(context.getField());

            Label uiLabel = findLabel(context.getField());
            if (uiLabel != null)
                renderer.renderLabel(context, uiLabel);

            TextInputControl uiField = findTextField(context.getField());
            if (uiField != null)
                renderer.renderTextComponent(context, uiField);
        }

        private FieldRenderer findRenderer(Field field) {
            FieldRenderer renderer = rendererCache.get(field.name());
            if (renderer == null) {
                String rendererClassName = Config.instance().getFieldRenderer(field.name());
                if (rendererClassName != null) {
                    try {
                        renderer = (FieldRenderer) Class.forName(rendererClassName).newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else
                    renderer = new DefaultFieldRenderer();
                rendererCache.put(field.name(), renderer);
            }
            return renderer;
        }

        public void setLessonFieldsEnabled(boolean enabled) {
            lbLesson.setDisable(!enabled);
            cbLessonShowOption.setDisable(!enabled);
        }

        public void setLessonFieldFileRef(File fileRef) {
            lessonFieldRenderer.renderLessonComponent(fileRef, tfLesson);
        }

        public void setLessonFieldShow(boolean showLessonFile) {
            cbLessonShowOption.setSelected(showLessonFile);
        }

        public void setRepetitionAddEnabled(boolean enabled) {
            btnRepetitionAdd.setDisable(!enabled);
            miRepetitionAdd.setDisable(!enabled);
        }

        public void setRepetitionModeEnabled(boolean enabled) {
            cbRepetitionMode.setDisable(!enabled);
        }

        public void setRepetitionCount(int count) {
            if (count == 0)
                cbRepetitionMode.setText(i18n.getString("nui.practice.repetition.activate"));
            else
                cbRepetitionMode.setText(
                        i18n.getString(
                                "nui.practice.repetition.activate.count",
                                Collections.singletonList(String.valueOf(count))
                        )
                );

        }

        public void setRepetitionMode(RepetitionMode repetitionMode) {
            cbRepetitionMode.setSelected(repetitionMode == RepetitionMode.ON);
        }

        public void setRepetitionAddVisible(boolean visible) {
            btnRepetitionAdd.setVisible(visible);
            miRepetitionAdd.setVisible(visible);
        }

        public void setRepetitionRemoveEnabled(boolean enabled) {
            btnRepetitionRemove.setDisable(!enabled);
            miRepetitionRemove.setDisable(!enabled);
        }

        public void setRepetitionRemoveVisible(boolean visible) {
            btnRepetitionRemove.setVisible(visible);
            miRepetitionRemove.setVisible(visible);
        }

        public void setRepetitionClearEnabled(boolean enabled) {
            btnRepetitionClear.setDisable(!enabled);
            miRepetitionClear.setDisable(!enabled);
        }

        public void setRepetitionClearVisible(boolean visible) {
            btnRepetitionClear.setVisible(visible);
            miRepetitionClear.setVisible(visible);
        }

        public void setPracticeProgress(PracticeProgress progress) {
            if (progress.isEmpty()) {
                pbVocab.setProgress(0);
                statusBar.setText(i18n.getString("nui.status.default"));
            } else {
                pbVocab.setProgress(progress.getRatioCurrentOfTotal());
                String usageString = "";
                if (progress.isShowUsage()) {
                    usageString = i18n.getString(
                            "nui.practice.status.statistics",
                            Arrays.asList(
                                    progress.getUsageCount(),
                                    progress.getLastUsageText()
                            )
                    );
                }

                if (progress.isRepetitionMode())
                    statusBar.setText(
                            i18n.getString(
                                    "nui.practice.status.repetition.param",
                                    Arrays.asList(
                                            progress.getTotalNumberOfEntries(),
                                            progress.getCurrentPosition() + 1,
                                            progress.getRatioCurrentOfTotalText(),
                                            usageString)
                            )
                    );
                else
                    statusBar.setText(
                            i18n.getString(
                                    "nui.practice.status.param",
                                    Arrays.asList(
                                            progress.getNumberOfLessons(),
                                            progress.getTotalNumberOfEntries(),
                                            progress.getCurrentPosition() + 1,
                                            progress.getRatioCurrentOfTotalText(),
                                            usageString
                                    )
                            )
                    );
            }
        }

        public void firePracticeWindowOnFocusChanged() {
            fireOnFocusChangedTo(new FxTextFieldWithFocus(tfForeign));
        }

        public void firePracticeWindowOnUnFocus() {
            fireOnFocusChangeToNonEditableControl();
        }

        public void setNavigationReshuffleEnabled(boolean enabled) {
            btnReshuffle.setDisable(!enabled);
            miReshuffle.setDisable(!enabled);
        }

        public void setNavigationResetEnabled(boolean enabled) {
            btnNavigationReset.setDisable(!enabled);
            miNavigationReset.setDisable(!enabled);
        }

        @Override
        public void askGotoPosition(int maxGotoPosition) {
            FxDialogs.Action action = FxDialogs.create()
                    .title(i18n.getString("nui.practice.goto.title"))
                    .message(i18n.getString("nui.practice.goto.input", Collections.singletonList(maxGotoPosition)))
                    .showTextInput();

            if (action.getActionType() == FxDialogs.ActionType.OK)
                practiceController.onAskGotoPositionResult(action.getTextInput());
        }

        @Override
        public void sendRepetitionSavedSuccess(File file) {
            // ignore
        }
    }


}
