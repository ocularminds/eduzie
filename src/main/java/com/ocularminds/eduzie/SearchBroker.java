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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.JobKey;
import org.quartz.impl.StdSchedulerFactory;

import com.ocularminds.eduzie.service.TrafficSearchJob;
import com.ocularminds.eduzie.service.EventSearchJob;

// @todo Replace all Qaurtz jobs with Java 8 Concurrent Features
public class SearchBroker {

    public static final int URI_MALFORMED = 0;
    public static final int URI_NOT_EXIST = 1;

    Properties properties;
    Scheduler sched = null;
    private static Logger logger = Logger.getLogger("SearchBroker");

    private static SearchBroker broker;

    public static SearchBroker newInstance() {

        if (broker == null) {
            broker = new SearchBroker();
        }

        return broker;
    }

    public static void add(ExecutorService service, List<Future<SearchObjectCache>> tasks, int agent, String source, String attributes) {

        Future f;
        if (agent == SearchAgent.TRAFFIC_SEARCH_AGENT) {
            f = service.submit(new TrafficAgent(source, attributes));
            tasks.add(f);
        } else if (agent == SearchAgent.EVENT_SEARCH_AGENT) {

            f = service.submit(new EventAgent(source, attributes));
            tasks.add(f);
        } else if (agent == SearchAgent.SPORT_SEARCH_AGENT) {
            f = service.submit(new SportAgent(source, attributes));
            tasks.add(f);
        } else {
        }
    }

    /**
     * This is the only method exposed as the API call loads multiple
     * configuration files from specified URI an invokes a background
     * {@link SearchAgent} callable task which in turn does the action property
     * collection from various streams. This is the method that is exposed as
     * API call.
     *
     * @param resources
     * @param attributes
     * @param agent
     * @throws java.lang.InterruptedException
     * @params resourses String containg space separated uri where data will be
     * extracted from.
     * @params attribute String containg space separated keywords to look for.
     * @return List class containing the loaded SearchObjectCache.
     */
    public static List<SearchObjectCache> search(String resources, String attributes, int agent)
            throws InterruptedException {
        //"crawling data sources";
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<SearchObjectCache> records = new ArrayList<>();
        java.util.List<String> sources = Arrays.asList(resources.split(" "));
        List<Future<SearchObjectCache>> tasks = new ArrayList<>();
        try {

            //for(String source:sources){
            sources.stream().forEach(source -> add(service, tasks, agent, source, attributes));
            for (Future<SearchObjectCache> task : tasks) {
                records.addAll((List<SearchObjectCache>) task.get());
            }

            service.shutdown();
            service.awaitTermination(5, TimeUnit.SECONDS);

        } catch (Exception ex) {
            logger.info("error loading resources ");
            logger.log(Level.SEVERE, null, ex);

        } finally {
            if (!service.isTerminated()) {
                logger.info("Cancel non-finish tasks");
            }
            service.shutdownNow();
        }

        logger.info("Task is completed, let's check result");
        logger.info("Document search completed\n");
        return records;
    }

    private void checkConfiguration() {

        properties = new Properties();
        System.out.println("[eduzi]: Initializing search broker jobs configuration :");
        System.out.println("[eduzi]:Done initializing jobs configuration.");
    }

    public void shutdown() throws Exception {
        if (sched != null) {
            sched.shutdown();
        }
    }

    public void scheduleJobs() throws Exception {

        System.out.println("------- Initializing ----------------------");
        checkConfiguration();

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        sched = sf.getScheduler();
        sched.deleteJob(JobKey.jobKey("event-search-job", "search.broker.jobs"));
        sched.deleteJob(JobKey.jobKey("traffic-search-job", "search.broker.jobs"));
        //sched.deleteJob(JobKey.jobKey("sport-search-job",  "search.broker.jobs"));
        //sched.deleteJob(JobKey.jobKey("data-analysis-job", "search.broker.jobs"));

        System.out.println("------- Initialization Complete -----------");
        System.out.println("------- Scheduling Jobs -------------------");

        //Job #1 is scheduled to run every 3 minutes
        JobDetail evtJob = newJob(EventSearchJob.class).withIdentity("event-search-job", "search.broker.jobs").build();
        Trigger ET = newTrigger().withIdentity("event-trigger", "search.broker.jobs")
                .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(60 * 03).repeatForever())
                .build();

        JobDetail tfcJob = newJob(TrafficSearchJob.class).withIdentity("traffic-search-job", "search.broker.jobs").build();
        Trigger TT = newTrigger().withIdentity("traffic-trigger", "search.broker.jobs")
                .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(60 * 03).repeatForever())
                .build();

        /*JobDetail danJob = newJob(DataAnalysisJob.class).withIdentity("data-analysis-job", "search.broker.jobs").build();
		Trigger DT = newTrigger().withIdentity("analysis-trigger", "search.broker.jobs")
						  .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(60*15).repeatForever())
				          .build();

	    JobDetail spoJob = newJob(SportSearchJob.class).withIdentity("sport-search-job", "search.broker.jobs").build();
		Trigger ST = newTrigger().withIdentity("sport-trigger", "search.broker.jobs")
						  .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(60*10).repeatForever())
				          .build();*/
        sched.scheduleJob(tfcJob, TT);
        sched.scheduleJob(evtJob, ET);
        //sched.scheduleJob(spoJob, ST);
        //sched.scheduleJob(danJob, DT);

        sched.start();
        System.out.println("[eduzi] all jobs started.");
    }

    public static void main(String[] args) {
        SearchBroker broker = SearchBroker.newInstance();
        try {
            broker.scheduleJobs();
        } catch (Exception e) {
            System.out.println("error starting jobs..");
            e.printStackTrace();
        }
    }
}
