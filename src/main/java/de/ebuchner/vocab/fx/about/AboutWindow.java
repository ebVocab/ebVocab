package de.ebuchner.vocab.fx.about;

import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.config.VocabEnvironment;
import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import de.ebuchner.vocab.model.update.AppInfo;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutWindow extends FxBaseWindow {
    private static final Logger LOGGER = Logger.getLogger(AboutWindow.class.getName());
    AboutWindowController controller = new AboutWindowController();
    @FXML
    private Label lbAppIcon;
    @FXML
    private Button btnUpdateCheck;
    @FXML
    private Label lbUpdateCheck;
    @FXML
    private Label lbAppVersion;
    @FXML
    private Label lbAppRuntime;
    @FXML
    private Label lbAppUI;
    @FXML
    private Label lbAppOS;
    @FXML
    private Hyperlink hlAppWebsite;
    @FXML
    private Button btnExitAndUpdate;

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.about.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "about";
    }


    @Override
    protected void onStageCreated() {
        lbAppIcon.setGraphic(new ImageView(new Image(ConfigConstants.APPLICATION_ICON_RESOURCE_NAME)));
        lbAppVersion.setText(VocabEnvironment.APP_VERSION);
        lbAppRuntime.setText(VocabEnvironment.javaRuntimeName());
        lbAppUI.setText(UIPlatformFactory.getUIPlatform().uiRuntimeName());
        lbAppOS.setText(VocabEnvironment.os());
        hlAppWebsite.setText(VocabEnvironment.APP_WEBSITE);
        btnExitAndUpdate.setText(
                i18n.getString(
                        "nui.about.exit.and.update",
                        Collections.singletonList(VocabEnvironment.APP_TITLE_SHORT)
                )
        );
    }

    @Override
    protected boolean isVocabWindowModal() {
        return true;
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
        return WindowType.ABOUT_WINDOW;
    }

    public void onUpdateCheck(ActionEvent actionEvent) {
        controller.onCheckForUpdates();
    }

    public void onAppWebSite(ActionEvent actionEvent) {
        controller.onWebsiteClick();
    }

    public void onExitAndUpdate(ActionEvent actionEvent) {
        controller.onExitAndUpdate();
    }

    public void onClose(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CLOSED);
    }

    protected void onBeforeShow() {
        btnUpdateCheck.fire();
    }

    class AboutWindowController {
        void onWebsiteClick() {
            try {
                VocabEnvironment.openBrowser(new URL(VocabEnvironment.APP_WEBSITE));
            } catch (Exception e) {
                LOGGER.log(Level.INFO, e.toString(), e);
            }
        }

        void onCheckForUpdates() {
            btnUpdateCheck.setDisable(true);
            btnExitAndUpdate.setDisable(true);
            lbUpdateCheck.setText(i18n.getString("nui.about.up.to.date.check.checking"));

            Task<AppInfo> task = new Task<AppInfo>() {
                @Override
                protected AppInfo call() throws Exception {
                    return AppInfo.createAppInfo();
                }

                @Override
                protected void succeeded() {
                    AppInfo appInfo = getValue();
                    System.out.printf(
                            "Remote version: %s - Local version: %s%n",
                            appInfo.getRemoteVersion(), appInfo.getLocalVersion()
                    );
                    if (appInfo.getRemoteVersion().isNoVersion()) {
                        lbUpdateCheck.setText(
                                i18n.getString(
                                        "nui.about.up.to.date.check.error"
                                )
                        );

                    } else if (appInfo.getRemoteVersion().isNewerThan(appInfo.getLocalVersion())) {
                        lbUpdateCheck.setText(
                                i18n.getString(
                                        "nui.about.up.to.date.check.outdated",
                                        Collections.singletonList(appInfo.getRemoteVersion().formatVersion())
                                )
                        );
                        btnExitAndUpdate.setDisable(false);
                        btnExitAndUpdate.setVisible(true);

                    } else
                        lbUpdateCheck.setText(
                                i18n.getString(
                                        "nui.about.up.to.date.check.ok",
                                        Collections.singletonList(appInfo.getRemoteVersion().formatVersion())
                                )
                        );
                    btnUpdateCheck.setDisable(false);
                }

                @Override
                protected void failed() {
                    btnUpdateCheck.setDisable(false);
                    lbUpdateCheck.setText(i18n.getString(
                            "nui.about.up.to.date.check.error"
                    ));
                    throw new RuntimeException(getException());
                }
            };
            new Thread(task).start();
        }

        void onExitAndUpdate() {
            btnUpdateCheck.setDisable(true);
            btnExitAndUpdate.setDisable(true);
            onWebsiteClick();
            UIPlatformFactory.getUIPlatform().getNuiDirector().shutDown();
        }
    }

}
