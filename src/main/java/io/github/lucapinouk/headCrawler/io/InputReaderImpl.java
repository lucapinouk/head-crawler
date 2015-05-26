package io.github.lucapinouk.headCrawler.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of a reader for the crawler inputs
 */
public class InputReaderImpl implements InputReader {
    private BufferedReader reader;

    /**
     * Creates a InputReader from a given file
     * @param path the file containing the inputs
     * @throws IOException If a I/O error occurs opening the file
     */
    public InputReaderImpl(Path path) throws IOException {
        reader = Files.newBufferedReader(path, Charset.defaultCharset());
    }

    /**
     * Creates a InputReader from a given input stream.
     * This allows to read the input from sources different from a file
     * (e.g., a String, the Standard Input)
     * @param is the input stream to read from
     */
    public InputReaderImpl(InputStream is) {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public URL getNextUrl() throws IOException {
        String line = reader.readLine();

        /* end of input */
        if(line == null) return null;

        try {
            URL url = new URL(line);

            /* if the HTTP URL is correctly parsed, return it */
            if (url.getProtocol()!=null && url.getProtocol().equals("http"))
                return url;

        }catch (MalformedURLException ignored){  }

        /* else the line is not a correct HTTP URL, skip it */
        return getNextUrl();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
