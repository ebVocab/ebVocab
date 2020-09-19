package de.ebuchner.vocab.model.practice;

import de.ebuchner.vocab.config.fields.Field;
import de.ebuchner.vocab.model.lessons.RepetitionMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SimplePracticeWindow implements PracticeWindowBehaviour {
    private boolean strategyOptionsEnabled;
    private SelectedStrategy selectedStrategy;

    private boolean navigationForwardEnabled;
    private boolean navigationBackwardEnabled;

    private Map<Field, Boolean> fieldsEnabled = new HashMap<Field, Boolean>();
    private Map<String, FieldRendererContext> renderedFieldMap = new HashMap<String, FieldRendererContext>();

    private boolean reverseOptionEnabled;
    private PracticeReverse reverseOption;

    private boolean lessonFieldsEnabled;
    private File lessonFieldFileRef;
    private boolean lessonFieldShow;

    private boolean repetitionModeEnabled;
    private RepetitionMode repetitionMode;
    private boolean repetitionAddEnabled;
    private boolean repetitionAddVisible;
    private boolean repetitionRemoveEnabled;
    private boolean repetitionRemoveVisible;
    private boolean repetitionClearEnabled;
    private boolean repetitionClearVisible;
    private int repetitionCount;
    private PracticeProgress practiceProgress;
    private int onFocusChanged;
    private int onUnFocus;

    public boolean isRepetitionAddEnabled() {
        return repetitionAddEnabled;
    }

    public void setRepetitionAddEnabled(boolean repetitionAddEnabled) {
        this.repetitionAddEnabled = repetitionAddEnabled;
    }

    public boolean isStrategyOptionsEnabled() {
        return strategyOptionsEnabled;
    }

    public void setStrategyOptionsEnabled(boolean strategyOptionsEnabled) {
        this.strategyOptionsEnabled = strategyOptionsEnabled;
    }

    public void setFieldEnabled(Field field, boolean enabled) {
        fieldsEnabled.put(field, enabled);
    }

    public SelectedStrategy getSelectedStrategy() {
        return selectedStrategy;
    }

    public void setSelectedStrategy(SelectedStrategy selectedStrategy) {
        this.selectedStrategy = selectedStrategy;
    }

    public boolean isNavigationForwardEnabled() {
        return navigationForwardEnabled;
    }

    public void setNavigationForwardEnabled(boolean navigationForwardEnabled) {
        this.navigationForwardEnabled = navigationForwardEnabled;
    }

    public boolean isNavigationBackwardEnabled() {
        return navigationBackwardEnabled;
    }

    public void setNavigationBackwardEnabled(boolean navigationBackwardEnabled) {
        this.navigationBackwardEnabled = navigationBackwardEnabled;
    }

    public boolean isReverseOptionEnabled() {
        return reverseOptionEnabled;
    }

    public void setReverseOptionEnabled(boolean reverseOptionEnabled) {
        this.reverseOptionEnabled = reverseOptionEnabled;
    }

    public PracticeReverse getReverseOption() {
        return reverseOption;
    }

    public void setReverseOption(PracticeReverse reverseOption) {
        this.reverseOption = reverseOption;
    }

    @Override
    public void setImageOptionEnabled(boolean enabled) {

    }

    @Override
    public void setImageOption(PracticeImage imageOption) {

    }

    public void setShowStatisticsOptionEnabled(boolean enabled) {

    }

    public void setShowStatisticsOption(boolean showStatistics) {

    }

    public void renderField(FieldRendererContext context) {
        renderedFieldMap.put(context.getField().name(), context);
    }

    public Map<String, FieldRendererContext> getRenderedFieldMap() {
        return renderedFieldMap;
    }

    public Map<Field, Boolean> getFieldsEnabled() {
        return fieldsEnabled;
    }

    public boolean isLessonFieldsEnabled() {
        return lessonFieldsEnabled;
    }

    public void setLessonFieldsEnabled(boolean lessonFieldsEnabled) {
        this.lessonFieldsEnabled = lessonFieldsEnabled;
    }

    public File getLessonFieldFileRef() {
        return lessonFieldFileRef;
    }

    public void setLessonFieldFileRef(File lessonFieldFileRef) {
        this.lessonFieldFileRef = lessonFieldFileRef;
    }

    public boolean isLessonFieldShow() {
        return lessonFieldShow;
    }

    public void setLessonFieldShow(boolean lessonFieldShow) {
        this.lessonFieldShow = lessonFieldShow;
    }

    public boolean isRepetitionModeEnabled() {
        return repetitionModeEnabled;
    }

    public void setRepetitionModeEnabled(boolean repetitionModeEnabled) {
        this.repetitionModeEnabled = repetitionModeEnabled;
    }

    public RepetitionMode getRepetitionMode() {
        return repetitionMode;
    }

    public void setRepetitionMode(RepetitionMode repetitionMode) {
        this.repetitionMode = repetitionMode;
    }

    public boolean isRepetitionAddVisible() {
        return repetitionAddVisible;
    }

    public void setRepetitionAddVisible(boolean repetitionAddVisible) {
        this.repetitionAddVisible = repetitionAddVisible;
    }

    public boolean isRepetitionRemoveEnabled() {
        return repetitionRemoveEnabled;
    }

    public void setRepetitionRemoveEnabled(boolean repetitionRemoveEnabled) {
        this.repetitionRemoveEnabled = repetitionRemoveEnabled;
    }

    public boolean isRepetitionRemoveVisible() {
        return repetitionRemoveVisible;
    }

    public void setRepetitionRemoveVisible(boolean repetitionRemoveVisible) {
        this.repetitionRemoveVisible = repetitionRemoveVisible;
    }

    public boolean isRepetitionClearEnabled() {
        return repetitionClearEnabled;
    }

    public void setRepetitionClearEnabled(boolean repetitionClearEnabled) {
        this.repetitionClearEnabled = repetitionClearEnabled;
    }

    public boolean isRepetitionClearVisible() {
        return repetitionClearVisible;
    }

    public void setRepetitionClearVisible(boolean repetitionClearVisible) {
        this.repetitionClearVisible = repetitionClearVisible;
    }

    public int getRepetitionCount() {
        return repetitionCount;
    }

    public void setRepetitionCount(int repetitionCount) {
        this.repetitionCount = repetitionCount;
    }

    public PracticeProgress getPracticeProgress() {
        return practiceProgress;
    }

    public void setPracticeProgress(PracticeProgress practiceProgress) {
        this.practiceProgress = practiceProgress;
    }

    public void firePracticeWindowOnFocusChanged() {
        this.onFocusChanged++;
    }

    public void firePracticeWindowOnUnFocus() {
        this.onUnFocus++;
    }

    public void setNavigationReshuffleEnabled(boolean enabled) {
    }

    public void setNavigationResetEnabled(boolean enabled) {
    }

    public void askGotoPosition(int maxGotoPosition) {
    }

    public void sendRepetitionSavedSuccess(File file) {
    }

    public int getOnFocusChanged() {
        return onFocusChanged;
    }

    public int getOnUnFocus() {
        return onUnFocus;
    }
}
