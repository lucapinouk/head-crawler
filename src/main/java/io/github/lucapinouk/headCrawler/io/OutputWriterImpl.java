package io.github.lucapinouk.headCrawler.io;

import io.github.lucapinouk.headCrawler.client.URLStatus;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Implementation of a writer for the crawler outputs
 */
public class OutputWriterImpl implements OutputWriter {
    private BufferedWriter writer;

    /**
     * Creates a OutputWriter from a given file
     * @param path the file where to write the outputs
     * @throws IOException If a I/O error occurs opening the file
     */
    public OutputWriterImpl(Path path) throws IOException {
        writer = Files.newBufferedWriter(path, Charset.defaultCharset(), CREATE, WRITE, TRUNCATE_EXISTING);
    }
    /**
     * Creates a OutputWriter from a given output stream.
     * This allows to write the output to destinations different from a file
     * (e.g., a String, the Standard Output)
     * @param os the output stream to write to
     */
    public OutputWriterImpl(OutputStream os) {
        writer = new BufferedWriter(new OutputStreamWriter(os));
    }

    @Override
    public void writeResult(URLStatus urlStatus) throws IOException {
        String line = urlStatus.getUrl() + "\t" + urlStatus.getStatus();
        writer.write(line, 0, line.length());
        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
