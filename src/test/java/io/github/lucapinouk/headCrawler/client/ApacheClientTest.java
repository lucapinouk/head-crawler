package io.github.lucapinouk.headCrawler.client;

import org.junit.BeforeClass;

public class ApacheClientTest extends ClientTest {
    private static Client client;
    private static Client timeoutClient;

    @BeforeClass
    public static void createClients() {
        client = new ApacheClient(1000);
        timeoutClient = new ApacheClient(1);
    }
    protected Client getClient(){
        return client;
    }
    protected Client getTimeoutClient(){
        return timeoutClient;
    }

}
