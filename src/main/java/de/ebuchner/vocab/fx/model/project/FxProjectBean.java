package de.ebuchner.vocab.fx.model.project;

import de.ebuchner.vocab.config.ConfigConstants;
import de.ebuchner.vocab.model.project.ProjectBean;
import de.ebuchner.vocab.model.project.ProjectConfiguration;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FxProjectBean extends ProjectBean implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(FxProjectBean.class.getName());

    public void loadProjectBean() {
        File projectBeanFile = projectBeanFile();
        if (!projectBeanFile.exists())
            return;

        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream(projectBeanFile));
            try {
                ProjectBean projectBean = (ProjectBean) xmlDecoder.readObject();
                File chd = new File(projectBean.getCurrentHomeDirectory());
                if (ProjectConfiguration.isValidProjectDirectory(chd))
                    this.setCurrentHomeDirectory(chd.getAbsolutePath());

                this.getRecentHomeDirectories().clear();
                for (String recentDirectory : projectBean.getRecentHomeDirectories()) {
                    File rhd = new File(recentDirectory);
                    if (ProjectConfiguration.isValidProjectDirectory(rhd))
                        this.getRecentHomeDirectories().add(rhd.getAbsolutePath());
                }
            } finally {
                xmlDecoder.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.toString(), e);
        }
    }

    public void saveProjectBean() {
        File projectBeanFile = projectBeanFile();
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream(projectBeanFile));
            try {
                xmlEncoder.writeObject(this);
            } finally {
                xmlEncoder.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File projectBeanFile() {
        return new File(System.getProperty("user.home"), ConfigConstants.PROJECT_BEAN_FILENAME);
    }

}
