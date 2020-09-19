package de.ebuchner.vocab.model.update;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VocabVersion {

    public static final VocabVersion NO_VERSION = new VocabVersion();
    private final static Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)[ bBeEtTaA]*");
    private final int majorVersion;
    private final int minorVersion;
    private final int subMinorVersion;

    private VocabVersion() {
        this(0, 0, 0);
    }

    public VocabVersion(int majorVersion, int minorVersion, int subMinorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.subMinorVersion = subMinorVersion;
    }

    public static VocabVersion parseVersion(String version) {
        Matcher m = VERSION_PATTERN.matcher(version);
        if (!m.matches())
            return NO_VERSION;

        int majorVersion = Integer.parseInt(m.group(1));
        int minorVersion = Integer.parseInt(m.group(2));
        int subMinorVersion = Integer.parseInt(m.group(3));

        return new VocabVersion(majorVersion, minorVersion, subMinorVersion);
    }

    public boolean isNewerThan(VocabVersion version) {
        return uniqueVersionNumber() > version.uniqueVersionNumber();
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getSubMinorVersion() {
        return subMinorVersion;
    }

    private int uniqueVersionNumber() {
        return majorVersion * 1000 + minorVersion * 100 + subMinorVersion;
    }

    @Override
    public int hashCode() {
        return uniqueVersionNumber();
    }

    public String formatVersion() {
        return String.format(Locale.ENGLISH, "%d.%d.%d", majorVersion, minorVersion, subMinorVersion);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof VocabVersion))
            return false;

        VocabVersion vocabVersion = (VocabVersion) obj;
        return uniqueVersionNumber() == vocabVersion.uniqueVersionNumber();
    }

    public boolean isNoVersion() {
        return this.equals(NO_VERSION);
    }

    @Override
    public String toString() {
        if (isNoVersion())
            return "NO VERSION INFO AVAILABLE";
        else
            return formatVersion();
    }
}
