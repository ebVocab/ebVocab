package de.ebuchner.vocab.fx.cloud;

import de.ebuchner.vocab.fx.common.FxBaseWindow;
import de.ebuchner.vocab.fx.common.FxDialogs;
import de.ebuchner.vocab.model.cloud.CloudModel;
import de.ebuchner.vocab.model.nui.NuiCloseEvent;
import de.ebuchner.vocab.model.nui.NuiClosingResult;
import de.ebuchner.vocab.model.nui.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CloudLoginWindow extends FxBaseWindow {
    @FXML
    private TextField tfServer;
    @FXML
    private TextField tfUser;
    @FXML
    private PasswordField tfSecret;
    @FXML
    private Button btnCloudConnect;
    @FXML
    private Button btnCancel;

    private CloudLoginWindowController loginWindowController = new CloudLoginWindowController();
    private CloudModel cloudModel;

    @Override
    protected boolean isVocabWindowModal() {
        return true;
    }

    @Override
    protected void onStageCreated() {
        cloudModel = CloudModel.getOrCreateCloudModel();
        if (cloudModel.getLastServer() != null)
            tfServer.setText(cloudModel.getLastServer());
        if (cloudModel.getLastUserName() != null)
            tfUser.setText(cloudModel.getLastUserName());
        if (cloudModel.getLastPassword() != null)
            tfSecret.setText(cloudModel.getLastPassword());
    }

    @Override
    protected String vocabWindowTitleKey() {
        return "nui.cloud.login.title";
    }

    @Override
    protected String vocabWindowResourcePrefix() {
        return "cloudLogin";
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
        return WindowType.CLOUD_LOGIN_WINDOW;
    }

    public void onCloudConnect(ActionEvent actionEvent) {
        loginWindowController.onCloudConnect();
    }

    public void onCancel(ActionEvent actionEvent) {
        windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.CANCEL);
    }

    class CloudLoginWindowController {

        public void onCloudConnect() {
            if (!verifyConnectionInput())
                return;

            cloudModel.setLastServer(tfServer.getText());
            cloudModel.setLastUserName(tfUser.getText());
            cloudModel.setLastPassword(tfSecret.getText());

            windowClosingController.doWindowClosing(NuiCloseEvent.CloseType.OK);
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

    }
}
