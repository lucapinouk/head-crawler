package io.github.lucapinouk.headCrawler;

import io.github.lucapinouk.headCrawler.crawler.Crawler;
import io.github.lucapinouk.headCrawler.crawler.CrawlerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class offering a CLI to the crawler
 * Contains the main method executed by the library
 */
public class TerminalApp {

    /**
     * Parses the CLI arguments
     * @param args the CLI arguments to parse. The expected arguments are:
     * <pre><code>[-a] &lt;input-file&gt; &lt;output-file&gt; &lt;max-threads&gt; &lt;timeout-seconds&gt;</code></pre>
     * @throws IllegalArgumentException if any of the arguments cannot be parsed or number of arguments is wrong
     */
    public void startApp(String[] args) throws IllegalArgumentException{
        if(args.length < 4)
            throw new IllegalArgumentException("ERROR: Wrong number of arguments.");

        int i = 0;
        boolean useBasic = true;

        if("-a".equals(args[i])){
            if(args.length != 5)
                throw new IllegalArgumentException("ERROR: Wrong number of arguments.");
            i++;
            useBasic = false;
        }

        Path inputPath = Paths.get(args[i++]);
        if(!Files.exists(inputPath) || !Files.isReadable(inputPath))
            throw new IllegalArgumentException("ERROR: Unable to read "+inputPath.toString());

        Path outputPath = Paths.get(args[i++]);
        if(!Files.exists(outputPath)) {
            try {
                Files.createFile(outputPath);
            } catch (IOException e) {
                throw new IllegalArgumentException("ERROR: Unable to create "+outputPath.toString());
            }
        }
        if(!Files.isWritable(outputPath))
            throw new IllegalArgumentException("ERROR: Unable to write "+outputPath.toString());

        int threadNumber;
        try{
            threadNumber = Integer.parseUnsignedInt(args[i++]);
        }catch (NumberFormatException ex){
            throw new IllegalArgumentException("ERROR: Unrecognised format for <max-threads>");
        }


        int timeoutSeconds;
        try{
            timeoutSeconds = Integer.parseUnsignedInt(args[i]);
        }catch (NumberFormatException ex){
            throw new IllegalArgumentException("ERROR: Unrecognised format for <max-threads>");
        }

        try {

            /* actual execution */
            Crawler crawler = getCrawlerFactory()
                    .createCrawler(useBasic, inputPath, outputPath, threadNumber, timeoutSeconds);
            crawler.start();

        } catch (InterruptedException e) {
            System.err.println("ERROR: Process interrupted");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("ERROR: Error accessing the files");
            System.exit(1);
        }
    }

    protected CrawlerFactory getCrawlerFactory() {
        return CrawlerFactory.getInstance();
    }

    public static void main( String[] args ) {
        try {
            new TerminalApp().startApp(args);
        }catch (IllegalArgumentException e){
            showUsageAndQuit(e.getMessage());
        }
    }
    private static void showUsageAndQuit(String error){
        System.err.println(error);
        System.err.println("Usage: crawler.sh [-a] <input-file> <output-file> <max-threads> <timeout-sec>");
        System.err.println("Option: -a to use the Apache HTTP Components based client");
        System.err.println("\t(otherwise the basic JDK client is used)");
        System.exit(1);
    }
}
