package io.github.lucapinouk.headCrawler.crawler;

import io.github.lucapinouk.headCrawler.client.ClientTaskBuilder;
import io.github.lucapinouk.headCrawler.client.URLStatus;
import io.github.lucapinouk.headCrawler.client.Client;
import io.github.lucapinouk.headCrawler.io.InputReader;
import io.github.lucapinouk.headCrawler.io.OutputWriter;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Implementation of the crawler
 */
public class CrawlerImpl implements Crawler {
    private int threadNo;
    private InputReader reader;
    private OutputWriter writer;
    private ClientTaskBuilder clientTaskBuilder;

    private boolean isRunning;
    /* The Semaphore is used to control the amount of tasks sent to the ExecutorService, to avoid using all the memory */
    private Semaphore availableThreads;
    private ExecutorService threadPool;
    private CompletionService<URLStatus> completionService;

    /**
     * Internal constructor. Use the CrawlerFactory to construct an object of this class.
     * @see CrawlerFactory
     */
    protected CrawlerImpl(Client client, int threadNo, InputReader reader, OutputWriter writer){
        this.threadNo = threadNo;
        this.reader = reader;
        this.writer = writer;
        this.clientTaskBuilder = new ClientTaskBuilder(client);

        this.availableThreads = new Semaphore(threadNo);
        this.threadPool = Executors.newFixedThreadPool(threadNo);
        this.completionService = new ExecutorCompletionService<>(threadPool);
        this.isRunning = false;
    }

    @Override
    public void start() throws InterruptedException {
        /* wait forever for completion */
        start(0l);
    }

    @Override
    public void start(long maximumWaitingTime) throws InterruptedException {
        if(isRunning) return;
        isRunning = true;

        try {
            /* Secondary thread for the dispatcher, used to save results when they are ready
               and to release threads from the pool */
            Thread resultsHandler = new Thread(new HandleResults(writer));
            resultsHandler.start();

            try {

                for (URL url = reader.getNextUrl(); url != null; url = reader.getNextUrl()) {
                    /* if all the threads are taken, wait until a thread is released
                       (to not fill the memory up with the whole file at once) */
                    availableThreads.acquire();
                    completionService.submit(clientTaskBuilder.createClientTask(url));
                }

            } catch (IOException ex) {
                System.err.println("FATAL: Error reading the input source.");
            }

            /* signal that no more tasks are going to be added to the ExecutorService */
            threadPool.shutdown();

            resultsHandler.join(maximumWaitingTime);

        } finally {
            try {
                threadPool.shutdownNow();
                reader.close();
                writer.close();
            } catch (IOException ignored) {  }
        }
    }

    /* Secondary thread for the dispatcher, used to collect and save results when they are ready.
       It releases a thread from the pool every time a new result is ready */
    private class HandleResults implements Runnable {
        private OutputWriter writer;

        public HandleResults(OutputWriter writer){
            this.writer = writer;
        }

        @Override
        public void run() {
            /* continuously retrieve results from the completed threads,
               until no new tasks are accepted and all the threads in the pool are available */
            while(!(threadPool.isShutdown() && availableThreads.availablePermits()==threadNo)){
                try {
                    /* wait for a result to become available */
                    Future<URLStatus> result = completionService.take();
                    writer.writeResult(result.get());

                    availableThreads.release();
                } catch (ExecutionException e) {
                    System.err.println("ERROR: Unexpected error using the client.");
                    availableThreads.release();
                } catch (InterruptedException | CancellationException e) {
                    System.err.println("ERROR: Task cancelled before completion.");
                    availableThreads.release();
                } catch (IOException e) {
                    System.err.println("FATAL: I/O Error during writing.");
                    break;
                }
            }
        }
    }

}
