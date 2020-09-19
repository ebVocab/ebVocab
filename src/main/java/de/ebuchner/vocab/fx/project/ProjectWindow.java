package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.config.VocabLanguage;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.cloud.CloudProjectController;
import de.ebuchner.vocab.model.cloud.CloudProjectWindowBehaviour;
import de.ebuchner.vocab.model.cloud.ProjectItem;
import de.ebuchner.vocab.model.cloud.ProjectList;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.project.ProjectController;
import de.ebuchner.vocab.model.project.ProjectWindowBehaviour;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

public class ProjectWindow extends FxBaseWindow {

    ProjectWindowController projectWindowController = new ProjectWindowController();
    SupportedLanguageModel supportedLanguageModel = new SupportedLanguageModel();
    @FXML
    private ComboBox<VocabLanguage> cbSelectedLanguage;
    @FXML
    private TextField tfProjectDir;
    @FXML
    private ComboBox<File> cbRecent;
    @FXML
    private Button btnProjectUse;
    @FXML
    private Button btnCheckoutRefresh;
    @FXML
    private ComboBox<ProjectItem> cbCheckoutProjects;
    @FXML
    private TextField tfCheckoutDir;
    @FXML
    private ProgressBar pbProject;
    private MyBehaviour myBehaviour = new MyBehaviour();
    private ProjectController projectController = new ProjectController(myBehaviour);
    private CloudProjectController cloudProjectController = new CloudProjectController(myBehaviour);

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.project.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "project";
    }

    @Override
    protected void onStageCreated() {
        initializeRecentProjects();
        initializeSelectedLanguages();
        initializeCloudProjects();

        pbProject.setProgress(0);
    }

    private void initializeRecentProjects() {
        RecentProjectsModel recentProjectsModel = RecentProjectsModel.createModel();
        cbRecent.getItems().addAll(recentProjectsModel.getRecentProjectDirectories());
        RecentProjectsCellFactory.customize(cbRecent);
        if (cbRecent.getItems().isEmpty()) {
            cbRecent.setDisable(true);
            btnProjectUse.setDisable(true);
        }

    }

    private void initializeSelectedLanguages() {
        cbSelectedLanguage.getItems().addAll(supportedLanguageModel.languages());
        SupportedLanguageCellFactory.customize(cbSelectedLanguage);
    }

    private void initializeCloudProjects() {
        ProjectItemCellFactory.customize(cbCheckoutProjects);
    }

    @Override
    protected NuiClosingResult onVocabWindowClosing(NuiCloseEvent.CloseType closeType) {
        return NuiClosingResult.CLOSING_OK;
    }

    @Override
    protected void onVocabWindowClosed(NuiCloseEvent.CloseType closeType) {

    }

    @Override
    public WindowType windowType() {
        return WindowType.PROJECT_WINDOW;
    }

    public void onShowHint(ActionEvent actionEvent) {
        projectWindowController.onShowHint();
    }

    public void onNewProject(ActionEvent actionEvent) {
        projectWindowController.onNewProject(ProjectDirType.LANGUAGE);
    }

    public void onChooseTargetLocation(ActionEvent actionEvent) {
        projectWindowController.onChooseTargetLocation(ProjectDirType.LANGUAGE);
    }

    public void onProjectUse(ActionEvent actionEvent) {
        projectController.onUseProject(
                cbRecent.getSelectionModel().getSelectedItem()
        );
    }

    public void onProjectSelectExisting(ActionEvent actionEvent) {
        projectController.onOpenProject();
    }

    public void onClose(ActionEvent actionEvent) {
        attemptClosing();
    }

    public void onCheckoutRefresh(ActionEvent actionEvent) {
        cloudProjectController.onRefreshProjects();
        if (!cbCheckoutProjects.getItems().isEmpty())
            cbCheckoutProjects.getSelectionModel().select(0);
    }

    public void onCheckout(ActionEvent actionEvent) {
        projectWindowController.onNewProject(ProjectDirType.CLOUD);
    }

    public void onCheckoutDirLocation(ActionEvent actionEvent) {
        projectWindowController.onChooseTargetLocation(ProjectDirType.CLOUD);
    }

    enum ProjectDirType {
        LANGUAGE, CLOUD
    }

    class ProjectWindowController {

        private File parentDir = null;

        public void onShowHint() {
            FxDialogs.create()
                    .title(i18n.getString("nui.project.tooltip"))
                    .message(i18n.getString("nui.project.hint.project.tooltip"))
                    .showInformation();
        }

        public void onNewProject(ProjectDirType projectDirType) {
            File selectedProjectDir = validateProjectSelection(projectDirType);
            if (selectedProjectDir == null)
                return;

            String code;
            if (projectDirType == ProjectDirType.LANGUAGE)
                code = (cbSelectedLanguage.getSelectionModel().getSelectedItem()).getCode();
            else
                code = cbCheckoutProjects.getSelectionModel().getSelectedItem().getProjectLocale();

            if (!projectController.onNewProject(
                    selectedProjectDir,
                    code))
                return;

            if (projectDirType == ProjectDirType.CLOUD) {
                checkoutFromCloud(selectedProjectDir);
            } else
                projectController.onUseProject(selectedProjectDir);
        }

        private void checkoutFromCloud(File selectedProjectDir) {
            enableUI(false);
            new Thread(new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    cloudProjectController.onCheckoutProject(
                            selectedProjectDir,
                            cbCheckoutProjects.getSelectionModel().getSelectedItem()
                    );
                    return null;
                }

                @Override
                protected void failed() {
                    enableUI(true);
                }

                @Override
                protected void succeeded() {
                    projectController.onUseProject(selectedProjectDir);
                    enableUI(true);
                }
            }).start();
        }

        private void enableUI(boolean enable) {
            cbSelectedLanguage.setDisable(!enable);
            tfProjectDir.setDisable(!enable);
            cbRecent.setDisable(!enable);
            btnProjectUse.setDisable(!enable);
            btnCheckoutRefresh.setDisable(!enable);
            cbCheckoutProjects.setDisable(!enable);
            tfCheckoutDir.setDisable(!enable);
            if (enable)
                pbProject.setProgress(0);
            else
                pbProject.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }

        private File validateProjectSelection(ProjectDirType projectDirType) {
            File selectedProjectDir = null;
            StringBuilder hints = new StringBuilder();

            ComboBox comboBox = projectDirType == ProjectDirType.LANGUAGE ?
                    cbSelectedLanguage : cbCheckoutProjects;
            TextField tfDir = projectDirType == ProjectDirType.LANGUAGE ?
                    tfProjectDir : tfCheckoutDir;

            if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
                if (hints.length() > 0)
                    hints.append("\n");
                hints.append(i18n.getString("nui.project.missing.language"));
            }

            if (tfDir.getText().trim().length() == 0) {
                if (hints.length() > 0)
                    hints.append("\n");
                hints.append(i18n.getString("nui.project.missing.parent.dir"));
            }

            if (hints.length() == 0) {
                selectedProjectDir = new File(tfDir.getText());
                if (selectedProjectDir.exists()) {
                    if (hints.length() > 0)
                        hints.append("\n");
                    hints.append(i18n.getString("nui.project.dir.exists"));
                }
            }

            if (hints.length() > 0) {
                FxDialogs.create()
                        .title(i18n.getString("nui.project.title"))
                        .message(hints.toString())
                        .showInformation();
                return null;
            }

            return selectedProjectDir;
        }

        public void onChooseTargetLocation(ProjectDirType projectDirType) {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle(i18n.getString("nui.project.choose.directory.title"));
            File selectedDir = dc.showDialog(stage);
            if (selectedDir == null)
                return;
            parentDir = selectedDir;
            updateNewProjectUI(projectDirType);
        }

        private void updateNewProjectUI(ProjectDirType projectDirType) {
            TextField tfDir = (projectDirType == ProjectDirType.LANGUAGE) ? tfProjectDir : tfCheckoutDir;
            tfDir.setText("");

            File projectDir = buildProjectDirectory(projectDirType);
            if (projectDir != null)
                try {
                    tfDir.setText(projectDir.getCanonicalPath());
                } catch (IOException e) {
                    tfDir.setText(projectDir.getAbsolutePath());
                }
            else if (parentDir != null)
                tfDir.setText(i18n.getString("nui.project.choose.language.hint"));
        }

        private File buildProjectDirectory(ProjectDirType projectDirType) {

            if (projectDirType == ProjectDirType.LANGUAGE &&
                    cbSelectedLanguage.getSelectionModel().getSelectedItem() == null)
                return null;
            if (projectDirType == ProjectDirType.CLOUD &&
                    cbCheckoutProjects.getSelectionModel().getSelectedItem() == null)
                return null;
            if (parentDir == null)
                return null;

            if (projectDirType == ProjectDirType.LANGUAGE)
                return new File(parentDir, (cbSelectedLanguage.getSelectionModel().getSelectedItem()).getDisplayName());
            else
                return new File(parentDir, (cbCheckoutProjects.getSelectionModel().getSelectedItem()).getProjectName());
        }
    }

    class MyBehaviour implements ProjectWindowBehaviour, CloudProjectWindowBehaviour {

        public void doClose() {
            ProjectWindow.this.attemptClosing();
        }

        public File doOpenProjectDirectory() {
            return projectDirectory("nui.project.open.title");
        }

        private File projectDirectory(String titleRes) {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle(i18n.getString(titleRes));
            return dc.showDialog(stage);
        }

        public void sendNewProjectDirInvalidMessage() {
            FxDialogs.create()
                    .title(i18n.getString("nui.project.create.error.title"))
                    .message(i18n.getString("nui.project.create.error.message"))
                    .showInformation();
        }

        public void sendExistingProjectDirInvalidMessage() {
            FxDialogs.create()
                    .title(i18n.getString("nui.project.open.error.title"))
                    .message(i18n.getString("nui.project.open.error.message"))
                    .showInformation();
        }

        @Override
        public void updateProjectList(ProjectList cloudProjectList) {
            cbCheckoutProjects.getItems().clear();

            for (ProjectItem item : cloudProjectList.getProjectItems()) {
                cbCheckoutProjects.getItems().add(item);
            }
        }
    }
}
