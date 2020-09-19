package de.ebuchner.vocab.fx.cloud;

import de.ebuchner.toolbox.i18n.I18NContext;
import de.ebuchner.vocab.model.cloud.CloudActionI18NKey;
import de.ebuchner.vocab.model.cloud.CloudTransfer;
import de.ebuchner.vocab.model.cloud.FileListAction;
import de.ebuchner.vocab.nui.common.I18NLocator;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CloudActionTableCell extends TableCell<CloudTableRow, FileListAction> {
    private final CloudTransferProvider cloudTransferProvider;
    private I18NContext i18n = I18NLocator.locate();

    private Image downloadImage = new Image(
            "/de/ebuchner/vocab/nui/res/download.png"
    );
    private Image uploadImage = new Image(
            "/de/ebuchner/vocab/nui/res/upload.png"
    );
    private Image stopImage = new Image(
            "/de/ebuchner/vocab/nui/res/stop.png"
    );

    public CloudActionTableCell(CloudTransferProvider cloudTransferProvider) {
        this.cloudTransferProvider = cloudTransferProvider;
    }

    protected void updateItem(FileListAction action, boolean empty) {
        super.updateItem(action, empty);
        if (action == null)
            return;

        String name = CloudActionI18NKey.findKeyFor(
                cloudTransferProvider.getCloudTransfer(),
                action.getActionType()
        );
        Image image = null;

        if (cloudTransferProvider.getCloudTransfer() == CloudTransfer.UPLOAD) {
            switch (action.getActionType()) {
                case SERVER_ONLY:
                    image = stopImage;
                    break;
                case LOCAL_ONLY:
                    image = uploadImage;
                    break;
                case SERVER_NEWER:
                    image = stopImage;
                    break;
                case LOCAL_NEWER:
                    image = uploadImage;
                    break;
            }
        } else if (cloudTransferProvider.getCloudTransfer() == CloudTransfer.DOWNLOAD) {
            switch (action.getActionType()) {
                case SERVER_ONLY:
                    image = downloadImage;
                    break;
                case LOCAL_ONLY:
                    image = stopImage;
                    break;
                case SERVER_NEWER:
                    image = downloadImage;
                    break;
                case LOCAL_NEWER:
                    image = stopImage;
                    break;
            }
        }

        setText(i18n.getString(name));
        setGraphic(new ImageView(image));
    }

    public interface CloudTransferProvider {
        CloudTransfer getCloudTransfer();
    }
}
