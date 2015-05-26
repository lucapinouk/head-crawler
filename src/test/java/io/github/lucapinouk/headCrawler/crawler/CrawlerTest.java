package io.github.lucapinouk.headCrawler.crawler;

import io.github.lucapinouk.headCrawler.client.URLStatus;
import io.github.lucapinouk.headCrawler.client.Client;
import io.github.lucapinouk.headCrawler.io.InputReader;
import io.github.lucapinouk.headCrawler.io.OutputWriter;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CrawlerTest {
    private static final URLStatus EXAMPLE_STATUS = new URLStatus("http://www.example.com", "200");
    private static final int N_INPUTS = 100;
    private static final int N_THREADS = 10;

    private List<URLStatus> executeCrawler(long timeout) throws InterruptedException {
        OutputWriterMock writerMock = new OutputWriterMock();
        Crawler crawler = new CrawlerImpl(new ClientStub(), N_THREADS, new InputReaderStub(false), writerMock);

        if(timeout==0)
            crawler.start();
        else
            crawler.start(timeout);

        return writerMock.processedOutputs;
    }

    @Test
    public void testCrawlerNoExceptionsIndefinitelyWait() throws InterruptedException {
        List<URLStatus> outputs = executeCrawler(0);

        assertEquals(N_INPUTS, outputs.size());
        for(URLStatus urlStatus : outputs)
            assertEquals(EXAMPLE_STATUS, urlStatus);
    }
    @Test
    public void testCrawlerNoExceptions() throws InterruptedException {
        List<URLStatus> outputs = executeCrawler(100000);

        assertEquals(N_INPUTS, outputs.size());
        for(URLStatus urlStatus : outputs)
            assertEquals(EXAMPLE_STATUS, urlStatus);
    }

    @Test
    public void testCrawlerCancelledException() throws InterruptedException {
        List<URLStatus> outputs = executeCrawler(10);

        assertNotEquals(EXAMPLE_STATUS, outputs.size());
    }

    @Test
    public void testCrawlerReadException() throws InterruptedException {
        OutputWriterMock writerMock = new OutputWriterMock();
        Crawler crawler = new CrawlerImpl(new ClientStub(), N_THREADS, new InputReaderStub(true), writerMock);

        crawler.start();

        assertEquals(N_INPUTS, writerMock.processedOutputs.size());
        for(URLStatus urlStatus : writerMock.processedOutputs)
            assertEquals(EXAMPLE_STATUS, urlStatus);
    }


    private class ClientStub implements Client {
        @Override
        public URLStatus performHeadCall(URL url) throws IOException {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return new URLStatus("interrupted", "interrupted");
            }
            return EXAMPLE_STATUS;
        }
    }
    private class InputReaderStub implements InputReader {
        private int processedInputs = 0;
        private boolean throwExceptions;
        public InputReaderStub(boolean throwExceptions){
            this.throwExceptions = throwExceptions;
        }
        @Override
        public URL getNextUrl() throws IOException {
            if(processedInputs == N_INPUTS) {
                if(throwExceptions) throw new IOException();
                return null;
            }
            processedInputs++;
            return new URL("http://www.example.com/"+processedInputs);
        }
        @Override
        public void close() throws IOException { }
    }
    private class OutputWriterMock implements OutputWriter {
        public List<URLStatus> processedOutputs = new ArrayList<>();
        @Override
        public void writeResult(URLStatus urlStatus) throws IOException {
            processedOutputs.add(urlStatus);
        }
        @Override
        public void close() throws IOException { }
    }
}
