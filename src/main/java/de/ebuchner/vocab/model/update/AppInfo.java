package de.ebuchner.vocab.model.update;

import de.ebuchner.vocab.config.VocabEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInfo {
    private static final String REMOTE_APP_INFO_URL
            = "https://downloads.sourceforge.net/project/ebvocab/vocabAppInfo.txt";
    private static final String APP_VERSION_PROPERTY = "app.version";
    private static final Logger LOGGER = Logger.getLogger(AppInfo.class.getName());

    private VocabVersion remoteVersion = VocabVersion.NO_VERSION;
    private VocabVersion localVersion = VocabVersion.NO_VERSION;

    public static AppInfo createAppInfo() throws IOException {
        AppInfo appInfo = new AppInfo();

        try {
            Properties remoteProperties = readFromURL();

            String versionString = remoteProperties.getProperty(APP_VERSION_PROPERTY);
            if (versionString != null)
                appInfo.remoteVersion = VocabVersion.parseVersion(versionString);

        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.toString(), e);
        }

        appInfo.localVersion = VocabVersion.parseVersion(VocabEnvironment.APP_VERSION);

        return appInfo;
    }

    private static Properties readFromURL() throws Exception {
        Downloader downloader = new Downloader(REMOTE_APP_INFO_URL);
        try {
            return readFromStream(downloader.openStream());
        } finally {
            downloader.close();
        }
    }

    private static Properties readFromStream(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    public VocabVersion getRemoteVersion() {
        return remoteVersion;
    }

    public VocabVersion getLocalVersion() {
        return localVersion;
    }


}