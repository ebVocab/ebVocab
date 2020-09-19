package de.ebuchner.vocab.fx.cloud;

import de.ebuchner.vocab.config.ProjectInfo;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.cloud.*;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CloudWindow extends FxBaseWindow {

    @FXML
    private TextField tfServer;
    @FXML
    private TextField tfUser;
    @FXML
    private TextField tfSecret;
    @FXML
    private TableView<CloudTableRow> tableDiffer;
    @FXML
    private TableColumn<CloudTableRow, FileListAction> tcAction;
    @FXML
    private TableColumn<CloudTableRow, String> tcPath;
    @FXML
    private TableColumn<CloudTableRow, String> tcFile;
    @FXML
    private RadioButton rbUpload;
    @FXML
    private RadioButton rbDownload;
    @FXML
    private Button btnRefresh;
    @FXML
    private ProgressBar pbStatus;
    @FXML
    private Label lbStatus;
    @FXML
    private Button btnTransferSelected;
    @FXML
    private Button btnTransferAll;

    private boolean activeIO;
    private CloudWindowController cloudWindowController = new CloudWindowController();
    private CloudController cloudController = new CloudController(new MyBehaviour());
    private FileListDiffer uploadDiffer = new FileListDiffer();
    private FileListDiffer downloadDiffer = new FileListDiffer();
    private CloudTransfer selectedCloudTransfer = CloudTransfer.UPLOAD;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.cloud.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "cloud";
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        if (activeIO)
            return NuiClosingResult.CLOSING_NOT_ALLOWED;
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {
        CloudModel cloudModel = CloudModel.getOrCreateCloudModel();

        cloudModel.setLastUserName(tfUser.getText());
        cloudModel.setLastPassword(tfSecret.getText());
        cloudModel.setLastServer(tfServer.getText());
    }

    @Override
    public WindowType windowType() {
        return WindowType.CLOUD_WINDOW;
    }

    @Override
    protected void onStageCreated() {
        CloudModel cloudModel = CloudModel.getOrCreateCloudModel();

        tfServer.setText(cloudModel.getLastServer());
        tfUser.setText(cloudModel.getLastUserName());
        tfSecret.setText(cloudModel.getLastPassword());
        pbStatus.setProgress(0);

        tableDiffer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableDiffer.getSelectionModel().getSelectedItems().addListener((ListChangeListener<CloudTableRow>)
                        c -> cloudWindowController.refreshUI()
        );


        tcAction.setCellValueFactory(
                new PropertyValueFactory<>("action")
        );
        tcAction.setCellFactory(column -> new CloudActionTableCell(() -> selectedCloudTransfer));

        tcPath.setCellValueFactory(
                new PropertyValueFactory<>("path")
        );
        tcFile.setCellValueFactory(
                new PropertyValueFactory<>("fileName")
        );

        cloudWindowController.refreshUI();
    }

    public void onSwitchDirection(ActionEvent actionEvent) {
        cloudWindowController.onSwitchDirection();
    }

    public void onRefresh(ActionEvent actionEvent) {
        cloudWindowController.doRefresh();
    }

    public void onTransferSelected(ActionEvent actionEvent) {
        cloudWindowController.onTransfer(TransferSelected.SELECTED);
    }

    public void onTransferAll(ActionEvent actionEvent) {
        cloudWindowController.onTransfer(TransferSelected.ALL);
    }

    private enum TransferSelected {
        SELECTED, ALL
    }

    abstract class CloudTask extends Task<CloudResult> {
        public CloudTask() {
            activeIO(true);
        }

        private void activeIO(boolean active) {
            activeIO = active;
            cloudWindowController.refreshUI();
        }

        @Override
        protected void cancelled() {
            activeIO(false);
        }

        @Override
        protected void succeeded() {
            activeIO(false);
            cloudController.doUpdateUI(config().getProjectInfo(), getValue());
        }

        @Override
        protected void failed() {
            activeIO(false);
        }
    }

    class CloudWindowController {

        private void refreshUI() {
            if (activeIO)
                lbStatus.setText("");
            pbStatus.setDisable(!activeIO);
            pbStatus.setProgress(activeIO ? ProgressBar.INDETERMINATE_PROGRESS : 0);

            btnRefresh.setDisable(activeIO);

            rbUpload.setSelected(!activeIO && selectedCloudTransfer == CloudTransfer.UPLOAD);
            rbUpload.setDisable(activeIO);

            rbDownload.setSelected(!activeIO && selectedCloudTransfer == CloudTransfer.DOWNLOAD);
            rbDownload.setDisable(activeIO);

            btnTransferAll.setDisable(activeIO || tableDiffer.getItems().isEmpty());
            btnTransferSelected.setDisable(activeIO || tableDiffer.getSelectionModel().getSelectedItems().isEmpty());
        }

        void doRefresh() {
            if (!verifyConnectionInput())
                return;

            new Thread(new CloudTask() {
                @Override
                protected CloudResult call() throws Exception {
                    return cloudController.doRefreshCloud(
                            tfServer.getText(),
                            config().getProjectInfo()
                    );
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    if (rbUpload.isSelected() && uploadDiffer.getActions().size() == 0) {
                        if (downloadDiffer.getActions().size() > 0)
                            rbDownload.fire();
                    } else if (rbDownload.isSelected() && downloadDiffer.getActions().size() == 0) {
                        if (uploadDiffer.getActions().size() > 0)
                            rbUpload.fire();
                    }

                }
            }).start();
        }

        private boolean verifyConnectionInput() {
            if (tfServer.getText() == null || tfServer.getText().isEmpty()) {
                FxDialogs.create()
                        .title(i18n.getString("nui.cloud.title"))
                        .actions(FxDialogs.ActionType.OK)
                        .message(i18n.getString("nui.cloud.missing.connection.input"))
                        .showInformation();

                return false;
            }

            return true;
        }

        public void onTableDifferNeedsRefresh() {
            tableDiffer.getItems().clear();
            FileListDiffer differ = getFileListDiffer();
            for (FileListAction action : differ.getActions()) {
                CloudTableRow row = new CloudTableRow(action);
                tableDiffer.getItems().add(row);
            }

            refreshUI();
        }

        private FileListDiffer getFileListDiffer() {
            return rbUpload.isSelected() ? uploadDiffer : downloadDiffer;
        }

        public void onSwitchDirection() {
            selectedCloudTransfer = rbUpload.isSelected() ? CloudTransfer.UPLOAD : CloudTransfer.DOWNLOAD;
            onTableDifferNeedsRefresh();
        }

        public void onTransfer(TransferSelected transferSelected) {
            if (!verifyConnectionInput())
                return;

            new Thread(new CloudTask() {
                @Override
                protected CloudResult call() throws Exception {
                    FileListDiffer differ = getFileListDiffer();
                    if (transferSelected == TransferSelected.SELECTED) {
                        List<FileListAction> actions = tableDiffer.getSelectionModel().getSelectedItems().stream().map(
                                CloudTableRow::getAction).collect(Collectors.toList()
                        );
                        differ = FileListDiffer.createWithActions(differ, actions);
                    }
                    return cloudController.doTransferCloud(
                            tfServer.getText(),
                            config().getProjectInfo(),
                            differ,
                            selectedCloudTransfer
                    );
                }

                @Override
                protected void succeeded() {
                    tableDiffer.getSelectionModel().clearSelection();
                    tableDiffer.getItems().clear();
                    doRefresh();
                }
            }).start();
        }
    }

    class MyBehaviour extends DefaultCloudWindowBehaviour {
        public String getUserName() {
            return tfUser.getText();
        }

        public String getSecret() {
            return tfSecret.getText();
        }

        @Override
        protected void onUpdateProjectStatus(ProjectInfo projectInfo, ProjectStatus projectStatus) {

        }

        @Override
        protected void onUpdateStatusMessage(ProjectInfo projectInfo, String statusMessage) {
            lbStatus.setText(statusMessage);
        }

        @Override
        protected void onNewUploadDiffer(ProjectInfo projectInfo, FileListDiffer differ) {
            uploadDiffer = differ;
            cloudWindowController.onTableDifferNeedsRefresh();
        }

        @Override
        protected void onNewDownloadDiffer(ProjectInfo projectInfo, FileListDiffer differ) {
            downloadDiffer = differ;
            cloudWindowController.onTableDifferNeedsRefresh();
        }

        public boolean confirmOverwrite(CloudTransfer cloudTransfer) {
            FxDialogs.Action response = FxDialogs.create()
                    .title(i18n.getString("nui.cloud.confirm.overwrite.title"))
                    .actions(FxDialogs.ActionType.OK, FxDialogs.ActionType.CANCEL)
                    .message(i18n.getString("nui.cloud.confirm.overwrite"))
                    .showConfirm();

            return response.getActionType() == FxDialogs.ActionType.OK;
        }
    }

}
