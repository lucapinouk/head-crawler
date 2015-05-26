package io.github.lucapinouk.headCrawler.io;

import io.github.lucapinouk.headCrawler.client.URLStatus;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface defining a writer for the crawler outputs
 */
public interface OutputWriter extends Closeable {
    /**
     * Write the results for an URL in the outputs
     * @param urlStatus the crawler results that should be written
     * @throws IOException If an I/O error occurs
     */
    void writeResult(URLStatus urlStatus) throws IOException;
}
