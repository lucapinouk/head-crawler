package io.github.lucapinouk.headCrawler.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class InputReaderTest {
    private final static String WRONG_URLS_STRING = "http://www.acard.com/\n"+
            "smb://some_folder/\n"+
            "http://www.acer.com/\n"+
            "https://www.google.com/\n"+
            "not a URL\n"+
            "anotherLine\n"+
            "\n"+
            "http://www.activestate.com\n";

    @Test
    public void testSampleUrlsFile() throws Exception {
        Path file = Paths.get(ClassLoader.getSystemResource("sample_urls.txt").toURI());

        InputReader reader = new InputReaderImpl(file);

        long i = 0;
        while(reader.getNextUrl() != null){
            i++;
        }

        assertEquals(448, i);

        reader.close();
    }


    private void checkWrongUrls(InputReader reader) throws Exception {
        URL url = reader.getNextUrl();
        assertNotNull(url);
        assertEquals(new URL("http://www.acard.com/"), url);
        url = reader.getNextUrl();
        assertNotNull(url);
        assertEquals(new URL("http://www.acer.com/"), url);
        url = reader.getNextUrl();
        assertNotNull(url);
        assertEquals(new URL("http://www.activestate.com"), url);
        url = reader.getNextUrl();
        assertNull(url);

    }
    @Test
    public void testWrongUrlsFile() throws Exception {
        Path file = Paths.get(ClassLoader.getSystemResource("wrong_urls.txt").toURI());

        InputReader reader = new InputReaderImpl(file);
        checkWrongUrls(reader);
        reader.close();
    }

    @Test
    public void testWrongUrlsInputStream() throws Exception {
        InputReader reader = new InputReaderImpl(new ByteArrayInputStream(WRONG_URLS_STRING.getBytes()));
        checkWrongUrls(reader);
        reader.close();
    }


    @Test(expected = IOException.class)
    public void testIOError() throws Exception {
        Path file = Paths.get("not-existing.txt");

        new InputReaderImpl(file);
    }
}
