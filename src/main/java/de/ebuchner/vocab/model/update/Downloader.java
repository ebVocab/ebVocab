package de.ebuchner.vocab.model.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class Downloader {

    private final String urlSpec;
    private InputStream inputStream;
    private HttpURLConnection connection;

    public Downloader(String urlSpec) {
        this.urlSpec = urlSpec;
    }

    public InputStream openStream() throws IOException {
        if (inputStream != null)
            return inputStream;

        connection = (HttpURLConnection) new URL(urlSpec).openConnection();
        connection.setInstanceFollowRedirects(true);
        String location = connection.getHeaderField("Location");
        if (location != null)
            inputStream = new URL(location).openStream();
        else
            inputStream = connection.getInputStream();
        return inputStream;
    }

    public void close() throws IOException {
        try {
            if (inputStream != null)
                inputStream.close();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}

