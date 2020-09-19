package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.model.nui.platform.UIPlatformFactory;
import de.ebuchner.vocab.model.project.ProjectBean;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

public class RecentProjectsModel {

    private final Collection<File> recentProjectDirectories;

    public RecentProjectsModel(Collection<File> recentProjectDirectories) {
        this.recentProjectDirectories = recentProjectDirectories;
    }

    public static RecentProjectsModel createModel() {
        TreeSet<File> recentProjectDirectories = new TreeSet<>(
                (o1, o2) -> {
                    int result = o1.getName().compareTo(o2.getName());
                    if (result == 0)
                        result = o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                    return result;
                }
        );

        File currentHome = null;
        if (Config.projectInitialized()) {
            currentHome = Config.instance().getProjectInfo().getProjectDirectory();
        }

        ProjectBean projectBean = UIPlatformFactory.getUIPlatform().getProjectBean();
        for (String path : projectBean.getRecentHomeDirectories()) {
            File f = new File(path);
            if (f.exists() && f.isDirectory() && ProjectConfiguration.isValidProjectDirectory(f)) {
                if (currentHome == null || !f.equals(currentHome))
                    recentProjectDirectories.add(f);
            }
        }

        return new RecentProjectsModel(recentProjectDirectories);
    }

    public Collection<File> getRecentProjectDirectories() {
        return recentProjectDirectories;
    }
}
