package io.github.lucapinouk.headCrawler.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

/**
 * Interface defining a reader for the crawler inputs
 */
public interface InputReader extends Closeable {
    /**
     * Read the next url in the inputs, skipping the ones in the wrong format.
     * @return URL the parsed url, or null if the input is over
     * @throws IOException If an I/O error occurs
     */
    URL getNextUrl() throws IOException;
}
