package com.ocularminds.eduzie;

/**
 * SearchBroker.java code snipet:<br>
 * <pre>
 * String[] uri = {"classpath:resources/jdbc.properties",
 *                 "file:///temp/system.properties"};
 * List<SearchObjectCache> data = broker.search(url,attributes);
 *
 * @author Jejelowo Festus
 * @version 1.0 18/11/2015
 */
import com.ocularminds.eduzie.context.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;

import java.util.concurrent.ExecutionException;

public final class WebCrawler implements Crawler{

    public static final int URI_MALFORMED = 0;
    public static final int URI_NOT_EXIST = 1;

    Properties properties;
   List<Context> searchAgents;
    private static final Logger LOG = Logger.getLogger(WebCrawler.class.getName());

    public WebCrawler(final List<Context> agents) {
        this.searchAgents = agents;
    }
    
    @Override
    public  List<SearchObjectCache> crawl() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<SearchObjectCache> records = new ArrayList<>();        
        List<Future<List<SearchObjectCache>>> tasks = new ArrayList<>();
        try {
            searchAgents.forEach(agent -> {
                Future<List<SearchObjectCache>> task = service.submit(agent);
                tasks.add(task);
             });
            for (Future<List<SearchObjectCache>> task : tasks) {
                records.addAll((List<SearchObjectCache>) task.get());
            }
            service.shutdown();
            service.awaitTermination(5, TimeUnit.SECONDS);

        } catch (InterruptedException | ExecutionException ex) {
            LOG.info("error loading resources ");
            LOG.log(Level.SEVERE, null, ex);

        } finally {
            if (!service.isTerminated()) {
                LOG.info("Cancel non-finish tasks");
            }
            service.shutdownNow();
        }

        LOG.info("Task is completed, let's check result");
        LOG.info("Document search completed\n");
        return records;
    }
}
