package io.github.lucapinouk.headCrawler.client;

import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public abstract class ClientTest {
    protected abstract Client getClient();
    protected abstract Client getTimeoutClient();

    protected void checkClientTask(Client client, String urlString, int expectedStatus) throws Exception {
        checkClientTask(client, urlString, String.valueOf(expectedStatus));
    }
    protected void checkClientTask(Client client, String urlString, String expectedStatus) throws Exception {
        URLStatus status = new ClientTaskBuilder(client).createClientTask(new URL(urlString)).call();

        assertEquals(expectedStatus, status.getStatus());
    }

    @Test
    public void testOk() throws Exception {
        checkClientTask(getClient(), "http://www.bbc.co.uk", HttpURLConnection.HTTP_OK);
    }

    @Test
    public void testRedirect() throws Exception {
        checkClientTask(getClient(), "http://import.io", 301);
    }

    @Test
    public void testNotFound() throws Exception {
        checkClientTask(getClient(), "http://www.bbc.co.uk/non-existing-page", HttpURLConnection.HTTP_NOT_FOUND);
    }

    @Test
    public void testUnknownHost() throws Exception {
        checkClientTask(getClient(), "http://www.nonexistinghost.com", URLStatus.UNKNOWN_HOST);
    }

    @Test
    public void testConnectionError() throws Exception {
        checkClientTask(getClient(), "http://www.bbc.co.uk:25/", URLStatus.CONNECTION_ERROR);
    }

    @Test
    public void testTimeout() throws Exception {
        checkClientTask(getTimeoutClient(), "http://www.verizon.com", URLStatus.TIMEOUT);
    }

}
