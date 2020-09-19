package de.ebuchner.vocab.nui;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.io.File;
import java.io.IOException;

public class NuiStarter {
    private File projectDir;

    private File hindiProjectDir() {
        final String HINDI_PROJECT_DIR = "data\\sample\\Hindi";
        final String HINDI_PROJECT_DIR_IJ = "desktop\\" + HINDI_PROJECT_DIR;

        File dir = new File(HINDI_PROJECT_DIR);
        if (dir.exists() && dir.isDirectory())
            return dir;
        dir = new File(HINDI_PROJECT_DIR_IJ);
        if (dir.exists() && dir.isDirectory())
            return dir;

        throw new IllegalStateException(String.format("%s not found", HINDI_PROJECT_DIR));
    }

    public final void prepareProjectDir() {
        prepareProjectDir(hindiProjectDir(), PreferencesHandling.IGNORE);
    }

    public final void prepareProjectDir(PreferencesHandling resetPreferences) {
        prepareProjectDir(hindiProjectDir(), resetPreferences);
    }

    public final void prepareProjectDir(File projectDir, PreferencesHandling resetPreferences) {
        this.projectDir = projectDir;

        if (!projectDir.exists() || !projectDir.isDirectory()) {
            try {
                throw new IllegalArgumentException("Not a valid directory: " + projectDir.getCanonicalPath());
            } catch (IOException e) {
                throw new IllegalArgumentException("Not a valid directory: " + projectDir);
            }
        }

        ProjectConfiguration.startupWithProjectDirectory(projectDir);

        if (resetPreferences == PreferencesHandling.RESET)
            Config.instance().resetPreferences();
    }

    public File getProjectDir() {
        return projectDir;
    }

    public static enum PreferencesHandling {IGNORE, RESET}

}
