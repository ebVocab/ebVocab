package de.ebuchner.vocab.fx.search;

import de.ebuchner.vocab.config.fields.FieldFactory;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.fx.platform.FxUIPlatform;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryRef;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.search.*;
import de.ebuchner.vocab.nui.NuiDirector;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchWindow extends FxBaseWindow {

    @FXML
    private TextField tfSearchString;
    @FXML
    private Button btnSearch;
    @FXML
    private CheckBox cbCaseSensitive;
    @FXML
    private CheckBox cbWholeWords;
    @FXML
    private CheckBox cbRegularExpression;
    @FXML
    private CheckBox cbSearchInComments;
    @FXML
    private TableView<SearchTableRow> resultTable;
    @FXML
    private Button btnResultEdit;
    @FXML
    private ProgressBar pbSearch;
    @FXML
    private Label lbCount;
    @FXML
    private TableColumn<SearchTableRow, String> tcForeign;
    @FXML
    private TableColumn<SearchTableRow, String> tcUser;
    @FXML
    private TableColumn<SearchTableRow, String> tcFileName;

    private SearchWindowController searchWindowController = new SearchWindowController();
    private SearchController searchController = new SearchController(new MyBehaviour());
    private SearchWindowTableUI searchWindowTableUI;

    @Override
    protected void onStageCreated() {
        NuiDirector nuiDirector = FxUIPlatform.instance().getNuiDirector();

        resultTable.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<SearchTableRow>) c -> searchWindowController.resultTableSelectionChanged()
        );

        tcForeign.setCellValueFactory(
                new PropertyValueFactory<>("foreign")
        );
        tcForeign.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.FOREIGN)));

        tcUser.setCellValueFactory(
                new PropertyValueFactory<>("user")
        );
        tcUser.setText(nuiDirector.uiDescription(FieldFactory.getGenericField(FieldFactory.USER)));

        tcFileName.setCellValueFactory(
                new PropertyValueFactory<>("fileName")
        );
    }

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.search.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "search";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
        fireOnFocusChangeToNonEditableControl();
    }

    @Override
    public WindowType windowType() {
        return WindowType.SEARCH_WINDOW;
    }

    public void onSearch(ActionEvent actionEvent) {
        searchWindowController.onSearch();
    }

    public void onResultEdit(ActionEvent actionEvent) {
        searchWindowController.onResultEdit();
    }

    public void onClose(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    class MyBehaviour implements SearchWindowBehaviour {
        public SearchResultModelBehaviour decorateModel(SearchResultModelBehaviour searchModel) {
            searchWindowTableUI = new SearchWindowTableUI(searchModel);
            return searchWindowTableUI;
        }

        public void sendMessageNoSearchString() {
            FxDialogs.create()
                    .title(i18n.getString("nui.search.title"))
                    .actions(FxDialogs.ActionType.OK)
                    .message(i18n.getString("nui.search.message.no.search.string"))
                    .showInformation();
        }

        public void sendMessageResultCount(int resultCount) {
            lbCount.setText(
                    i18n.getString(
                            "nui.search.message.result.size",
                            Collections.singletonList(resultCount)
                    )
            );
            if (resultCount == 0)
                FxDialogs.create()
                        .title(i18n.getString("nui.search.title"))
                        .actions(FxDialogs.ActionType.OK)
                        .message(i18n.getString("nui.search.message.no.result"))
                        .showInformation();
            stage.sizeToScene();
        }

        public void setResultElementsSelectedCount(int selectedCount) {
            btnResultEdit.setDisable(selectedCount != 1);
        }

        public void setSearchOptions(SearchOptions searchOptions, boolean searchInComments) {
            cbCaseSensitive.setSelected(searchOptions.isCaseSensitive());
            cbRegularExpression.setSelected(searchOptions.isRegularExpression());
            cbWholeWords.setSelected(searchOptions.isWholeWords());
            cbSearchInComments.setSelected(searchInComments);
        }
    }

    class SearchWindowController {

        void resultTableSelectionChanged() {
            searchController.onTableSelectionChanged(findSelectedEntries());
        }

        private List<VocabEntry> findSelectedEntries() {
            List<VocabEntry> entries = new ArrayList<>();
            for (SearchTableRow row : resultTable.getSelectionModel().getSelectedItems()) {
                entries.add(row.getEntry());
            }
            return entries;
        }

        void onResultEdit() {
            searchController.onResultEdit(
                    searchWindowTableUI.getEntryRef(
                            resultTable.getSelectionModel().getSelectedIndex()
                    )
            );
        }

        void onSearch() {
            pbSearch.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            btnSearch.setDisable(true);

            final String searchString = tfSearchString.getText();
            final boolean wholeWords = cbWholeWords.isSelected();
            final boolean caseSensitive = cbCaseSensitive.isSelected();
            final boolean regEx = cbRegularExpression.isSelected();
            final boolean searchInComments = cbSearchInComments.isSelected();

            Task<VocabSearcher.Result> searchTask = new Task<VocabSearcher.Result>() {
                @Override
                protected VocabSearcher.Result call() throws Exception {
                    return searchController.onSearchStart(
                            searchString,
                            caseSensitive,
                            wholeWords,
                            regEx,
                            searchInComments
                    );
                }

                @Override
                protected void succeeded() {
                    try {
                        searchController.onSearchResult(getValue());
                    } finally {
                        resetUI();
                    }
                }

                @Override
                protected void cancelled() {
                    resetUI();
                }

                @Override
                protected void failed() {
                    getException().printStackTrace();
                    resetUI();
                }

                private void resetUI() {
                    btnSearch.setDisable(false);
                    pbSearch.setProgress(0);
                }
            };
            new Thread(searchTask).start();
        }
    }

    class SearchWindowTableUI implements SearchResultModelBehaviour {
        private final SearchResultModelBehaviour decoree;

        public SearchWindowTableUI(SearchResultModelBehaviour decoree) {
            this.decoree = decoree;
        }

        @Override
        public int getEntryCount() {
            return decoree.getEntryCount();
        }

        @Override
        public VocabEntryRef getEntryRef(int row) {
            return decoree.getEntryRef(row);
        }

        @Override
        public VocabEntry getEntry(int row) {
            return decoree.getEntry(row);
        }

        @Override
        public void setResult(VocabSearcher.Result result) {
            decoree.setResult(result);
            resultTable.getItems().clear();

            for (VocabEntryRef entryRef : result.getEntryRefs()) {
                VocabEntry entry = result.getEntry(entryRef);
                if (entry == null)
                    continue;
                resultTable.getItems().add(
                        new SearchTableRow(
                                entryRef,
                                entry
                        )
                );
            }
        }

        @Override
        public VocabSearchOptions getDefaultSearchOptions() {
            return decoree.getDefaultSearchOptions();
        }

        @Override
        public void setDefaultSearchOptions(VocabSearchOptions vocabSearchOptions) {
            decoree.setDefaultSearchOptions(vocabSearchOptions);
        }
    }
}
