package io.github.lucapinouk.headCrawler.crawler;

/**
 * Interface defining the crawler operations
 */
public interface Crawler {
    /**
     * Start to crawl, waiting forever for completion
     * @throws InterruptedException
     */
    void start() throws InterruptedException;

    /**
     * Start to crawl, waiting other threads until the given time for completion.
     * If the crawler has not completed by the given time, the method should return anyway.
     * @param maximumWaitingTime the time the main thread has to wait for the other threads to complete
     * @throws InterruptedException
     */
    void start(long maximumWaitingTime) throws InterruptedException;
}
