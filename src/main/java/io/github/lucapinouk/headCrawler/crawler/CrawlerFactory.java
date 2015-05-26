package io.github.lucapinouk.headCrawler.crawler;

import io.github.lucapinouk.headCrawler.client.ApacheClient;
import io.github.lucapinouk.headCrawler.client.BasicClient;
import io.github.lucapinouk.headCrawler.client.Client;
import io.github.lucapinouk.headCrawler.io.InputReader;
import io.github.lucapinouk.headCrawler.io.InputReaderImpl;
import io.github.lucapinouk.headCrawler.io.OutputWriter;
import io.github.lucapinouk.headCrawler.io.OutputWriterImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Factory class that allows creating Crawler objects
 */
public class CrawlerFactory {
    private static CrawlerFactory instance = null;
    protected CrawlerFactory() { }
    public static CrawlerFactory getInstance() {
        if(instance == null) {
            instance = new CrawlerFactory();
        }
        return instance;
    }

    /**
     * Create a crawler using the inputs and sending outputs to the files specified,
     * using the given number of worker threads.
     * The crawler will use the JDK based client.
     * @param inputPath input file
     * @param outputPath output file
     * @param threadNumber number of worker threads used
     * @param timeoutSeconds Seconds the client will wait for each request before declare a connection timeout
     * @return a crawler
     * @throws IOException If an I/O error occurs
     */
    public Crawler createCrawler(
            Path inputPath, Path outputPath, int threadNumber, int timeoutSeconds) throws IOException {

        return createCrawler(true, inputPath, outputPath, threadNumber, timeoutSeconds);
    }

    /**
     * Create a crawler using the inputs and sending outputs to the files specified,
     * using the given number of worker threads.
     * @param useBasic true to use the JDK based client, false to use the Apache HTTP Components based client
     * @param inputPath input file
     * @param outputPath output file
     * @param threadNumber number of worker threads used
     * @param timeoutSeconds Seconds the client will wait for each request before declare a connection timeout
     * @return a crawler
     * @throws IOException If an I/O error occurs
     */
   public Crawler createCrawler(
           boolean useBasic, Path inputPath, Path outputPath, int threadNumber, int timeoutSeconds) throws IOException {

       int timeoutMillis = timeoutSeconds * 1000;

       Client client;
       if(useBasic){
           client = new BasicClient(timeoutMillis);
       }else{
           client = new ApacheClient(timeoutMillis);
       }
       InputReader reader = new InputReaderImpl(inputPath);
       OutputWriter writer = new OutputWriterImpl(outputPath);

       return new CrawlerImpl(client, threadNumber, reader, writer);
   }
}
