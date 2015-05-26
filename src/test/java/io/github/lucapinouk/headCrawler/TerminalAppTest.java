package io.github.lucapinouk.headCrawler;

import io.github.lucapinouk.headCrawler.crawler.Crawler;
import io.github.lucapinouk.headCrawler.crawler.CrawlerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TerminalAppTest {
    // Arguments list: [-a] <input-file> <output-file> <max-threads> <timeout-seconds>

    @Test
    public void testTerminalAppNoFlag() throws IllegalArgumentException {
        TerminalAppMock mock = new TerminalAppMock();
        mock.startApp(new String[]{"sample_urls.txt", "terminal_out.txt", "10", "4"});

        assertEquals(Paths.get("sample_urls.txt"), mock.inputPath);
        assertEquals(Paths.get("terminal_out.txt"), mock.outputPath);
        assertEquals(Integer.valueOf(10), mock.threadNumber);
        assertEquals(Integer.valueOf(4), mock.timeoutSeconds);
        assertEquals(true, mock.useBasic);
    }
    @Test
    public void testTerminalAppApacheFlag() throws IllegalArgumentException {
        TerminalAppMock mock = new TerminalAppMock();
        mock.startApp(new String[]{"-a", "sample_urls.txt", "terminal_out.txt", "10", "4"});

        assertEquals(Paths.get("sample_urls.txt"), mock.inputPath);
        assertEquals(Paths.get("terminal_out.txt"), mock.outputPath);
        assertEquals(Integer.valueOf(10), mock.threadNumber);
        assertEquals(Integer.valueOf(4), mock.timeoutSeconds);
        assertEquals(false, mock.useBasic);
    }
    @Test
    public void testTerminalAppFileCreation() throws IllegalArgumentException {
        TerminalAppMock mock = new TerminalAppMock();
        mock.startApp(new String[]{"sample_urls.txt", "new_file.txt", "10", "4"});

        assertTrue(Files.exists(Paths.get("new_file.txt")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoArguments() {
        new TerminalAppMock().startApp(new String[0]);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testWrongFirstArgument() {
        new TerminalAppMock().startApp(new String[]{ "1", "2", "3", "4" });
    }
    @Test(expected = IllegalArgumentException.class)
    public void testWrongSecondArgument() {
        new TerminalAppMock().startApp(new String[]{ "sample_urls.txt", "unwritable_file.txt", "3", "4" });
    }
    @Test(expected = IllegalArgumentException.class)
    public void testWrongThirdArgument() {
        new TerminalAppMock().startApp(new String[]{ "sample_urls.txt", "terminal_out.txt", "wrong_input", "4" });
    }
    @Test(expected = IllegalArgumentException.class)
    public void testWrongFourthArgument() {
        new TerminalAppMock().startApp(new String[]{"sample_urls.txt", "terminal_out.txt", "10", "wrong_input" });
    }


    @BeforeClass
    public static void prepareFiles() throws Exception {
        Path file = Paths.get("unwritable_file.txt");
        if(!Files.exists(file)) Files.createFile(file);
        assert file.toFile().setWritable(false) : "Unable to change file permissions for " + file.toString();

        Files.deleteIfExists(Paths.get("terminal_out.txt"));
        Files.deleteIfExists(Paths.get("new_file.txt"));

        file = Paths.get(ClassLoader.getSystemResource("sample_urls.txt").toURI());
        Files.copy(file, Paths.get("sample_urls.txt"), StandardCopyOption.REPLACE_EXISTING);
    }
    @AfterClass
    public static void deleteTempFiles() throws Exception {
        Path file = Paths.get("unwritable_file.txt");
        if(Files.exists(file)) {
            assert file.toFile().setWritable(true) : "Unable to change file permissions for " + file.toString();
            Files.delete(file);
        }

        Files.deleteIfExists(Paths.get("terminal_out.txt"));
        Files.deleteIfExists(Paths.get("new_file.txt"));
        Files.deleteIfExists(Paths.get("sample_urls.txt"));
    }

    private class TerminalAppMock extends TerminalApp {
        Boolean useBasic = null;
        Path inputPath = null;
        Path outputPath = null;
        Integer threadNumber = null;
        Integer timeoutSeconds = null;

        @Override
        protected CrawlerFactory getCrawlerFactory(){
            return new CrawlerFactory(){
                @Override
                public Crawler createCrawler(boolean _useBasic, Path _inputPath, Path _outputPath,
                                                int _threadNumber, int _timeoutSeconds) throws IOException {
                    useBasic = _useBasic;
                    inputPath = _inputPath;
                    outputPath = _outputPath;
                    threadNumber = _threadNumber;
                    timeoutSeconds = _timeoutSeconds;

                    return new Crawler() {
                        @Override
                        public void start() throws InterruptedException { }
                        @Override
                        public void start(long maximumExecutionTime) throws InterruptedException { }
                    };
                }
            };
        }
    }

}