package io.github.lucapinouk.headCrawler.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

/**
 * This class creates Callable tasks that
 * perform the HTTP Head calls asynchronously
 */
public class ClientTaskBuilder {
    private Client client;

    /**
     * Create a builder that uses the given client
     * in the HTTP Head call tasks it creates
     * @param client the client to use to perform the HTTP Head calls
     */
    public ClientTaskBuilder(Client client){
        this.client = client;
    }

    /**
     * Create a Callable task that perform
     * a HTTP Head call over the given url
     * and parses the possible client exceptions
     * into appropriate errors
     * @param url the URL where to send the HTTP Head request
     * @return a task that, if called, returns the status of the response or an error message
     * @see Client
     */
    public Callable<URLStatus> createClientTask(final URL url) {
        return new Callable<URLStatus>() {

            @Override
            public URLStatus call() {
                try {

                    return client.performHeadCall(url);

                } catch (UnknownHostException e) {
                    return new URLStatus(url, URLStatus.UNKNOWN_HOST);
                } catch (InterruptedIOException e) {
                    return new URLStatus(url, URLStatus.TIMEOUT);
                } catch (IOException e) {
                    return new URLStatus(url, URLStatus.CONNECTION_ERROR);
                }
            }
        };

    }
}
