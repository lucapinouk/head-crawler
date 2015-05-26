package io.github.lucapinouk.headCrawler.client;

import java.net.URL;

/**
 * Class containing the results of crawling a URL.
 * It contains the URL crawled and the status,
 * that is either the HTTP response code or an error message
 * (a set of standard error messages is given as String constants in this class)
 */
public class URLStatus {
    public static final String CONNECTION_ERROR = "CONNECTION_ERR";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String UNKNOWN_HOST = "UNKNOWN_HOST";

    private String url;
    private String status;

    /**
     * Create a results of a crawled url
     * @param url visited URL, not null
     * @param status the status code of the HTTP response
     */
    public URLStatus(URL url, int status) {
        this(url, String.valueOf(status));
    }
    /**
     * Create a results of a crawled url
     * @param url visited URL, not null
     * @param status the status code of the HTTP response or an error message, not null
     */
    public URLStatus(URL url, String status) {
        if(url == null || status == null) throw new NullPointerException();

        this.url = url.toExternalForm();
        this.status = status;
    }
    /**
     * Create a results of a crawled url
     * @param url visited URL, not null
     * @param status the status code of the HTTP response or an error message, not null
     */
    public URLStatus(String url, String status) {
        if(url == null || status == null) throw new NullPointerException();

        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URLStatus urlStatus = (URLStatus) o;

        return url.equals(urlStatus.url) && status.equals(urlStatus.status);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }
}
