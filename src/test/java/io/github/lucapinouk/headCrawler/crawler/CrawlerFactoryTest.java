package io.github.lucapinouk.headCrawler.crawler;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CrawlerFactoryTest {
    private static final boolean FLAG_HUGE_TEST = false; // careful: may take one day to complete!
                                                   // total requests to check = (INPUT_LINES * HUGE_MULTIPLIER)
    private static final int HUGE_MULTIPLIER = 51200; // 680 MB file
    private static final int INPUT_LINES = 448;
    private static final int TIMEOUT_SEC = 3;

    private static Path input;
    private static Path outputBasic;
    private static Path outputApache;

    @Test
    public void testCrawlerFactorySingleton()  {
        CrawlerFactory factory1 = CrawlerFactory.getInstance();
        CrawlerFactory factory2 = CrawlerFactory.getInstance();

        assertSame(factory1, factory2);
    }

    private void checkIntegration(boolean useBasic, int threads, int totalLines) throws Exception {
        Path output = useBasic?outputBasic:outputApache;

        long startTime = System.currentTimeMillis();
        Crawler crawler = CrawlerFactory.getInstance()
                .createCrawler(useBasic, input, output, threads, TIMEOUT_SEC);
        crawler.start();
        long totalTime = System.currentTimeMillis() - startTime;

        long outputLines = 0l;
        BufferedReader reader = Files.newBufferedReader(output, Charset.defaultCharset());
        String line = reader.readLine();
        while(line != null){
            outputLines++;
            line = reader.readLine();
        }

        assertEquals(totalLines, outputLines);

        assertTrue(totalTime < totalLines * TIMEOUT_SEC * 1000);
    }

    @Test
    public void testIntegrationApache() throws  Exception {
        if(FLAG_HUGE_TEST)
            checkIntegration(false, 500, INPUT_LINES * HUGE_MULTIPLIER);
        else
            checkIntegration(false, 100, INPUT_LINES);
    }

    @Test
    public void testIntegrationBasic() throws  Exception {
        if(FLAG_HUGE_TEST)
            checkIntegration(true, 500, INPUT_LINES * HUGE_MULTIPLIER);
        else
            checkIntegration(true, 100, INPUT_LINES);
    }

    @BeforeClass
    public static void prepareFiles() throws Exception {
        Path originalInput = Paths.get(ClassLoader.getSystemResource("sample_urls.txt").toURI());
        if(FLAG_HUGE_TEST){
            input = Paths.get("huge_file.txt");

            if(!Files.exists(input)) {
                FileChannel originalInputChannel = FileChannel.open(originalInput, StandardOpenOption.READ);
                FileChannel hugeInputChannel = FileChannel.open(input, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                for (long i = 0; i < HUGE_MULTIPLIER; i++) {
                    originalInputChannel.transferTo(0, originalInputChannel.size(), hugeInputChannel);
                }
                originalInputChannel.close();
                hugeInputChannel.close();
            }

        }else {
            input = originalInput;
        }
        outputApache = Paths.get("output_apache.txt");
        outputBasic = Paths.get("output_basic.txt");

        Files.deleteIfExists(outputApache);
        Files.deleteIfExists(outputBasic);
    }
    @AfterClass
    public static void deleteTempFiles() throws Exception {
        if(FLAG_HUGE_TEST){
            Files.deleteIfExists(input);
        }

        Files.deleteIfExists(outputApache);
        Files.deleteIfExists(outputBasic);
    }
}
