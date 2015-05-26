# HEAD Crawler
Java-based multi-threaded crawler that performs HTTP HEAD requests for the given pages and returns resulting the status code

## Compile

The best way to compile the project is by using [Apache Maven](https://maven.apache.org/). Once you have Maven configured properly, go to your HEAD Crawler directory and run `mvn package`: 

```bash
$ cd head-crawler
$ mvn package
```

## Usage

After having compiled the project you can run HEAD Crawler by using the convenience shell script `crawler.sh` from a terminal. The program accepts one optional flag `-a` and four arguments:

* the optional `-a` changes the HTTP client used by the crawler from the basic JDK one to a client based on [Apache HttpComponents](https://hc.apache.org/)
* the position of the input file
* the position of the output file
* the number of worker threads that the crawler can use
* the timeout used for each HTTP connection, in seconds

> If you’re on *nix, make sure that the script is executable (otherwise do a chmod 755 crawler.sh).

Example:
```bash
$ ./crawler.sh examples/sample_urls.txt output.txt 200 3
```

You can also directly use `java -jar target/head-crawler-1.0.jar` to run the application:

```bash
$ java -jar target/head-crawler-1.0.jar examples/sample_urls.txt output.txt 200 3
```

## Documentation

The tool reads inputs regarding the HTTP URLs to crawl from the input file and writes the results of the respective HEAD requests in the output file. 

### Input

Each line of the input file represents a URL to crawl.

Example:

```
http://www.google.com
http://www.bbc.co.uk
http://github.com
http://www.nonexistinghost.com
```

### Output

Each line of the output file contains the crawled URL, a tabulation and the result of the HEAD call. 

* The lines are not in the same order of the input file ones.
* The result is either the HTTP response status code or one of the following error messages:
  * CONNECTION_ERR
  * TIMEOUT
  * UNKNOWN_HOST

Example (matching the input above):

```
http://www.bbc.co.uk	200
http://www.nonexistinghost.com  UNKNOWN_HOST
http://www.google.com	302
http://github.com	301
```

### JDK vs Apache HttpComponents client

As specified above, through the usage of a flag is possible to change the client used by the crawler.

The JDK based client uses [HttpURLConnection](http://docs.oracle.com/javase/7/docs/api/java/net/HttpURLConnection.html) to connect. For the purpose of HEAD requests, it seems to be generally faster, as it does not involve many objects, but it is very simple.

The [Apache HttpComponents](https://hc.apache.org/) based client is, instead, a little bit more advanced, supporting:

* reuse of a connection to a host for other requests (useful and faster if crawling resources on the same host)
* three automatic retries when a connection cannot be enstablished

## Licence

This software is licensed under the [MIT license](http://opensource.org/licenses/MIT).
