package io.github.lucapinouk.headCrawler.io;

import io.github.lucapinouk.headCrawler.client.URLStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class OutputWriterTest {
    private static final byte[] READ_ONLY_FILE_CONTENT = ("Read only file!" + System.lineSeparator()).getBytes();
    private static final byte[] EXPECTED_CONTENT =
            ("http://import.io\t301" + System.lineSeparator() +
            "http://www.bbc.com\t200" + System.lineSeparator() +
            "http://www.example.com\tTIMEOUT" + System.lineSeparator()).getBytes();

    private static URLStatus[] STATUS_ARRAY = {
            new URLStatus("http://import.io", "301"),
            new URLStatus("http://www.bbc.com", "200"),
            new URLStatus("http://www.example.com", "TIMEOUT")
    };

    private void writeExample(OutputWriter writer) throws IOException {
        for(URLStatus status : STATUS_ARRAY)
            writer.writeResult(status);
        writer.close();
    }

    @Test
    public void testWriteExistingFile() throws Exception {
        Path file = Paths.get(ClassLoader.getSystemResource("output_urls.txt").toURI());
        OutputWriter writer = new OutputWriterImpl(file);
        writeExample(writer);

        assertArrayEquals(EXPECTED_CONTENT, Files.readAllBytes(file));
    }

    @Test
    public void testWriteNewFile() throws Exception {
        Path file = Paths.get("newFile.txt");
        OutputWriter writer = new OutputWriterImpl(file);
        writeExample(writer);

        assertArrayEquals(EXPECTED_CONTENT, Files.readAllBytes(file));
    }

    @Test
    public void testWriteReadOnlyFile() throws Exception {
        Path file = Paths.get(ClassLoader.getSystemResource("read_only.txt").toURI());

        try (OutputWriter writer = new OutputWriterImpl(file)){
            writeExample(writer);

            fail("The writer has not thrown an exception while writing a read only file.");
        } catch (IOException e) {
            assert true;
        }

        assertArrayEquals(READ_ONLY_FILE_CONTENT, Files.readAllBytes(file));
    }


    @Test
    public void testOutputStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputWriter writer = new OutputWriterImpl(outputStream);
        writeExample(writer);

        assertArrayEquals(EXPECTED_CONTENT, outputStream.toByteArray());
    }

    @BeforeClass
    @AfterClass
    public static void prepareFiles() throws Exception {
        Path file = Paths.get(ClassLoader.getSystemResource("output_urls.txt").toURI());
        Files.write(file, "TEST".getBytes(), CREATE, WRITE);

        file = Paths.get(ClassLoader.getSystemResource("read_only.txt").toURI());
        assert file.toFile().setWritable(true) : "Unable to change file permissions for " + file.toString();
        Files.write(file, READ_ONLY_FILE_CONTENT, CREATE, WRITE);
        assert file.toFile().setWritable(false) : "Unable to change file permissions for " + file.toString();

        file = Paths.get("newFile.txt");
        Files.deleteIfExists(file);
    }

}
