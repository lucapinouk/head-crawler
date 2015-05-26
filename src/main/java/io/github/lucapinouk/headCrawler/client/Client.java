package io.github.lucapinouk.headCrawler.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Interface for HTTP Head request clients
 * allowing using different implementations.
 * The client should not follow redirects.
 */
public interface Client {
    /**
     * Perform a HTTP Head request to the given URL
     * and return the response status or an error.
     * @param url the URL where to send the head request
     * @return the response status or an error
     * @throws UnknownHostException If the URL host IP address could not be determined
     * @throws InterruptedIOException If a timeout has occurred
     * @throws IOException If a connection problem has occurred
     * @see URLStatus
     * @see ClientTaskBuilder
     */
    URLStatus performHeadCall(URL url) throws IOException;
}
