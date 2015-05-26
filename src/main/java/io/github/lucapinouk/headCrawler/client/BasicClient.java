package io.github.lucapinouk.headCrawler.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of the HTTP Head client using just the JDK library (i.e., relying on HttpURLConnection)
 * @see HttpURLConnection
 */
public class BasicClient implements Client {
    private int timeout;

    /**
     * Create a new client, using the given timeout for the connections.
     * @param timeout Connection timeout in milliseconds
     */
    public BasicClient(int timeout) {
        this.timeout = timeout;

        HttpURLConnection.setFollowRedirects(false);
    }

    @Override
    public URLStatus performHeadCall(URL httpUrl) throws IOException {
        HttpURLConnection connection = null;
        try {
            //Create connection
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setUseCaches(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            return new URLStatus(httpUrl, connection.getResponseCode());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
