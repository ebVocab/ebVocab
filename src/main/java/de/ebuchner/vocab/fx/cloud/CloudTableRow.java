package de.ebuchner.vocab.fx.cloud;

import de.ebuchner.vocab.model.cloud.FileListAction;

public class CloudTableRow {

    private FileListAction action;

    private String path;

    private String fileName;

    public CloudTableRow(FileListAction action) {
        this.action = action;

        switch (action.getActionType()) {
            case SERVER_ONLY:
                path = action.getRemoteItem().getRelativePath();
                fileName = action.getRemoteItem().getFileName();
                break;
            default:
                path = action.getLocalItem().getRelativePath();
                fileName = action.getLocalItem().getFileName();
                break;
        }
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public FileListAction getAction() {
        return action;
    }
}
