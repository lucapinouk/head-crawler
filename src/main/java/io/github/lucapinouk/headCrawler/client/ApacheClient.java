package io.github.lucapinouk.headCrawler.client;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Implementation of the HTTP Head client using the Apache HTTP Components
 * This client may reuse the same connection to the same host if multiple resources are requested.
 * This client automatically retries 3 times to connect if a connection error occurs.
 */
public class ApacheClient implements Client {
    /* the following line is used to disable the logging on the STDERR from the HTTPClient */
    static{  System.setProperty("org.apache.commons.logging.Log",
              "org.apache.commons.logging.impl.NoOpLog"); }

    private CloseableHttpClient httpClient;

    /**
     * Create a new client, using the given timeout for the connections.
     * @param timeout Connection timeout in milliseconds
     */
    public ApacheClient(int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setAuthenticationEnabled(false)
                .setRedirectsEnabled(false)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(5);
        cm.setMaxTotal(100);

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public URLStatus performHeadCall(URL url) throws IOException {

        try (CloseableHttpResponse response = httpClient.execute(new HttpHead(url.toURI()))) {

            return new URLStatus(url, response.getStatusLine().getStatusCode());

        } catch (HttpResponseException ex) {
            return new URLStatus(url, ex.getStatusCode());
        } catch (URISyntaxException e) {
            return new URLStatus(url, URLStatus.CONNECTION_ERROR);
        }
    }
}
