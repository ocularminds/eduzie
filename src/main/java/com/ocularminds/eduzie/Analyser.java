package com.ocularminds.eduzie;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.time.Month;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.ocularminds.eduzie.common.DateUtil;
import com.ocularminds.eduzie.common.FileUtil;
import com.ocularminds.eduzie.common.TextUtil;

import javax.xml.transform.stream.StreamResult;

import org.dmg.pmml.PMML;
//import org.dmg.pmml.ImportFilter;
import org.jpmml.model.ImportFilter;
import org.xml.sax.InputSource;
import javax.xml.transform.sax.SAXSource;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.visitors.LocatorNullifier;

public class Analyser {

    static Map<String, String> routes = new HashMap<String, String>();
    //static

    public static void analyse(List<String> data) {

        data.stream().filter(s -> s.startsWith("c"))
                .map(String::toUpperCase).sorted()
                .forEach(System.out::println);

    }

    public static void process() {

        Task task1 = new Task("Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 5), new String[]{"git", "book"}, false, LocalDate.of(2015, Month.JULY, 15));
        Task task2 = new Task("Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2), new String[]{"java8", "lamdas", "book"}, true, LocalDate.of(2015, Month.JULY, 2));
        Task task3 = new Task("Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.AUGUST, 12), new String[]{"mobile", "app"}, false, LocalDate.of(2015, Month.AUGUST, 15));
        Task task4 = new Task("Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2015, Month.JULY, 18), new String[]{"java8", "blog", "stream"}, false, LocalDate.of(2015, Month.JULY, 25));
        Task task5 = new Task("Read Domain Driven Design book", TaskType.READING, LocalDate.of(2015, Month.JULY, 10), new String[]{"book"}, false, LocalDate.of(2015, Month.JULY, 15));
        List<Task> tasks = Arrays.asList(task1, task2, task3, task4, task5);

        System.out.println("\nReading tasks by title");
        System.out.println("\n-----------------------");
        List<String> readingTasks = findAllReadingTitlesSortedByCreationDate(tasks);
        readingTasks.forEach(title -> System.out.println(title));
        //readingTasks.forEach(task -> System.out.println(task.getTitle()+" "+task.getCreatedOn()+" "+(task.isDone()?"Completed":"Pending")));

        List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(comparing(Task::getCreatedOn).reversed()).
                //skip(page * n) creating page number
                //limit(5). for top 5 records
                map(Task::getTitle).
                collect(toList());

        //using range
        IntStream.range(0, 10).forEach(i -> System.out.print(i + " "));

        //The rangeClosed method allows you to create streams that includes the upper bound as well.
        IntStream.rangeClosed(1, 10).forEach(i -> System.out.print(i + " "));

        //You can also create infinite streams using iterate method on the primitive streams as shown below.
        //LongStream infiniteStream = LongStream.iterate(1, el -> el + 1);
        //To filter out all even numbers in an infinite stream, we can write code as shown below.
        //infiniteStream.filter(el -> el % 2 == 0).forEach(System.out::println);
        //We can limit the resulting stream by using the limit operation as shown below.
        //infiniteStream.filter(el -> el % 2 == 0).limit(100).forEach(System.out::println);
        //Creating Streams from Arrays
        //You can create streams from arrays by using the static stream method on theArrays class as shown below.
        String[] tags = {"java", "git", "lambdas", "machine-learning"};
        Arrays.stream(tags).map(String::toUpperCase).forEach(System.out::println);

        //stream with start and end indexes as shown below.
        Arrays.stream(tags, 1, 3).map(String::toUpperCase).forEach(System.out::println);

        //Parallel Streams
        //String[] urls = {"https://www.google.co.in/", "https://twitter.com/", "http://www.facebook.com/"};
        // Arrays.stream(urls).parallel().map(url -> getUrlContent(url)).forEach(System.out::println);
        Map<TaskType, List<Task>> groupTasks = groupTasksByType(tasks);
        groupTasks.forEach((g, v) -> System.out.println(g.toString() + " " + v.toString()));

        System.out.println("\nSummary Statistics");
        IntSummaryStatistics summaryStatistics = tasks.stream().map(Task::getTitle)
                .collect(Collectors.summarizingInt(String::length));
        System.out.println("Average: " + summaryStatistics.getAverage()); //32.4
        System.out.println("Count  : " + summaryStatistics.getCount()); //5
        System.out.println("Max    : " + summaryStatistics.getMax()); //44
        System.out.println("Min    : " + summaryStatistics.getMin()); //24
        System.out.println("Sum    : " + summaryStatistics.getSum()); //162
    }

    //group by type
    private static Map<TaskType, List<Task>> groupTasksByType(List<Task> tasks) {
        return tasks.stream().collect(Collectors.groupingBy(task -> task.getType()));
    }

    /*The operations that need to be performed are:
	1.Filter all the tasks that have TaskType as READING.
	2.Sort the filtered values tasks by createdOn field.
	3.Get the value of title for each task.
	4.Collect the resulting titles in a List.
     */
    private static List<String> findAllReadingTitlesSortedByCreationDate(List<Task> tasks) {

        List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted((t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())).
                map(task -> task.getTitle()).
                collect(Collectors.toList());
        return readingTaskTitles;
    }

    /*
    Find all the unique tags from all tasks
	To find all the distinct tags we have to perform the following operations:
	1.Extract tags for each task.
	2.Collect all the tags into one stream.
	3.Remove the duplicates.
	4.Finally collect the result into a list.
     */
    private static List allDistinctTags(List<Task> tasks) {
        return tasks.stream().flatMap(task -> task.getTags().stream()).distinct().collect(toList());
    }

    /*
    To check if all reading titles have a tag with name books
     */
    private static boolean isAllReadingTasksWithTagBooks(List<Task> tasks) {
        return tasks.stream().
                filter(task -> task.getType() == TaskType.READING)
                .allMatch(task -> task.getTags().contains("books"));
    }

    // To check whether any reading task has a java8 tag,useing anyMatch operation.
    private static boolean isAnyReadingTasksWithTagJava8(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getType() == TaskType.READING)
                .anyMatch(task -> task.getTags().contains("java8"));
    }

    /*create a summary of all the titles using reduce operation.
	The reduce function takes a lambda which joins elements of the stream.
     */
    private static String joinAllTaskTitles(List<Task> tasks) {
        return tasks.stream().map(Task::getTitle).
                reduce((first, second) -> first + " *** " + second).get();
    }

    public static void createModel() throws Exception {

        String file = "insight.data.txt";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(file);
        PMML pmml = load(is);

        LocatorNullifier nullifier = new LocatorNullifier();
        nullifier.applyTo(pmml);

        OutputStream os = new FileOutputStream(new File("edizi.xml"));

        StreamResult result = new StreamResult(os);
        JAXBUtil.marshalPMML(pmml, result);
    }

    public static PMML load(InputStream is) throws Exception {

        InputSource source = new InputSource(is);
        SAXSource transformedSource = ImportFilter.apply(source);

        return JAXBUtil.unmarshalPMML(transformedSource);
    }

    public void fibonacci() {

        IntStream fs = Stream.iterate(new int[]{1, 1}, fib -> new int[]{fib[1], fib[0] + fib[1]}).mapToInt(fib -> fib[0]);
        fs.limit(10).forEach(fib -> System.out.print(fib + " "));

    }

    public boolean isPrime(long number) {
        return number > 1 && LongStream.rangeClosed(2, (long) Math.sqrt(number))
                .parallel().noneMatch(index -> number % index == 0);
    }

    public long factorial(int n) {
        return LongStream.rangeClosed(1, n).reduce((a, b) -> a * b).getAsLong();
    }

    public static String traffic(String text) {

        if (text.toLowerCase().contains("jam") || text.toLowerCase().contains("stand still")) {
            return "Heavy";
        } else if (text.toLowerCase().contains("broken down") || text.toLowerCase().contains("accident")) {
            return "Heavy";
        } else if (text.toLowerCase().contains("heavy traffic")) {
            return "Heavy";
        } else if (text.toLowerCase().contains("slow moving")) {
            return "Slow-Moving";
        } else if (text.toLowerCase().contains("slow")) {
            return "Slow";
        } else {
            return "Free";
        }
    }

    private static void addRoute(String route, String code) {

        if (routes.get(route) == null) {
            routes.put(route, code);
        }
    }

    private static String extractRoute(String s, Map<String, String> routes) {

        int from = s.toLowerCase().indexOf("from");
        int is = s.toLowerCase().indexOf("is");
        if (is < 0) {
            is = s.toLowerCase().indexOf("being");
        }
        if (is < from) {
            is = s.toLowerCase().indexOf("being");
        }
        if (is < from) {
            is = s.toLowerCase().indexOf("as");
        }
        if (from < 0) {
            from = s.toLowerCase().indexOf("at");
        }

        String route = null;
        try {
            route = (from > -1 && is > -1) ? s.substring(from + 5, is) : null;
        } catch (Exception e) {
            System.out.println(s + ".substring(" + from + "5," + is + ")");
        }

        if (route == null) {
            for (Entry<String, String> e : routes.entrySet()) {
                if (TextUtil.isSimilar(e.getKey(), s)) {
                    route = e.getKey();
                    break;
                }
            }
            if (route == null) {
                if (s.toLowerCase().contains("from")) {
                    route = s.substring(s.toLowerCase().indexOf("from"), s.length());
                } else {
                    route = s;
                }
            }
        }

        if (route.toLowerCase().contains("from")) {
            route = route.substring(route.toLowerCase().indexOf("from") + 5, route.length());
        }

        if (route.toLowerCase().contains("from a lil further from !")) {
            route = route.substring(route.toLowerCase().indexOf("from a lil further from !") + "from a lil further from !".length(), route.length());
        }

        if (route.toLowerCase().contains("a lil further from !")) {
            route = route.substring(route.toLowerCase().indexOf("a lil further from !") + "a lil further from !".length(), route.length());
        }

        return route;
    }

    public static void reduce(List<SearchObjectCache> data) {

        List<SearchObjectCache> search = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (!FileUtil.isFileExists("traffic_data.csv")) {
            sb.append(String.format("%s,%s,%s,%s\n", "ROUTE", "DAY", "TIME", "STATUS"));
        }

        for (SearchObjectCache o : data) {

            String route = extractRoute(o.getText(), routes);
            if (route.length() < 105) {

                if (route.length() > 70) {
                    route = route.substring(0, 70);
                }
                String code = routes.get(route);
                if (code == null) {

                    code = Integer.toString((int) (Math.random() * 8000 + 1000));
                    addRoute(route, code);

                }

                sb.append(String.format("%s,%s,%s.%s,%s\n", code, o.getDate().getDayOfWeek(), o.getDate().getHour(), o.getDate().getMinute(), traffic(o.getText())));
            }
        }

        String file = "traffic_data.csv";
        FileUtil.append(file, sb.toString());
        sb = new StringBuffer();
        for (Entry<String, String> e : routes.entrySet()) {
            sb.append(e.getValue()).append(",").append(e.getKey()).append("\n");
        }
        FileUtil.append("traffic_routes.csv", sb.toString());
    }

    //gather traffic data to include, attribute(slow,heavy,free,win,draw,loose),time and place(route,town,location,home,away)
    public static void reduce() {
        String[] data = {"Traffic,http://www.tsaboin.com, Be the first to tell your friends and colleagues about Tsaboin's LIVE #LagosTraffic Cameras.",
            "Traffic,http://www.tsaboin.com, From Palm Grove to Anthony Isale is Slow This traffic is serious from Stadium and Ojuelegba being slow up to Anthony",
            "Traffic,http://www.tsaboin.com, From Obanikoro to Onipanu is Slow Same situation inward !palmgrove >> !onipanu ..slow traffic all the way",
            "Traffic,http://www.tsaboin.com, From Onipanu to Anthony Isale is Slow Very slow traffic along Ikorodu road inward and outward !anthonyisale",
            "Traffic,http://www.tsaboin.com, From Second Rainbow to Mile 2 Oke is Slow Traffic is on the high along this route ..>> !mile2oke",
            "Traffic,http://www.tsaboin.com, From Anthony Isale to Obanikoro is Slow Slow traffic towards that side",
            "Traffic,http://www.tsaboin.com, This traffic is serious from Stadium and Ojuelegba being slow up to Anthony",
            "Traffic,http://www.tsaboin.com, Same situation inward !palmgrove >> !onipanu ..slow traffic all the way",
            "Traffic,http://www.tsaboin.com, Very slow traffic along Ikorodu road inward and outward !anthonyisale",
            "Slow,http://www.tsaboin.com, From Third Mainland Bridge to Iyana Oworo is Moving fine but slower at the Iyana Oworo as usual.",
            "Slow,http://www.tsaboin.com, From Palm Grove to Anthony Isale is Slow This traffic is serious from Stadium and Ojuelegba being slow up to Anthony",
            "Slow,http://www.tsaboin.com, From Costain to Ijora is Slow Slow movement inward !ijora causeway >> !barracksapapa",
            "Slow,http://www.tsaboin.com, From Obanikoro to Onipanu is Slow Same situation inward !palmgrove >> !onipanu ..slow traffic all the way",
            "Slow,http://www.tsaboin.com, From Onipanu to Anthony Isale is Slow Very slow traffic along Ikorodu road inward and outward !anthonyisale",
            "Slow,http://www.tsaboin.com, From Ojota to Maryland is Slow Slow movement inward !ojotanewgarage >> !marylandindepencetunnel",
            "Slow,http://www.tsaboin.com, From Anthony Isale to Obanikoro is Slow Slow traffic towards that side",
            "Slow,http://www.tsaboin.com, From Obanikoro to Onipanu is Slow Slow movement inward !palmgrove >> !onipanu",
            "Slow,http://www.tsaboin.com, fine but slower at the Iyana Oworo as usual.",
            "Slow,http://www.tsaboin.com, This traffic is serious from Stadium and Ojuelegba being slow up to Anthony",
            "Slow,http://www.tsaboin.com, Same situation inward !palmgrove >> !onipanu ..slow traffic all the way",
            "Slow,http://www.tsaboin.com, Very slow traffic along Ikorodu road inward and outward !anthonyisale",
            "Slow,http://www.tsaboin.com, Slow movement inward !ijora causeway >> !barracksapapa"};

        List<SearchObjectCache> search = new ArrayList<>();
        Map<String, String> routes = new HashMap<>();
        for (int x = 0; x < data.length; x++) {

            String[] s = data[x].split(",");
            String id = Integer.toString(x);
            String cat = s[0];
            String text = s[2];
            String source = s[1];
            String route = extractRoute(text, routes);
            //java.util.Date dd = new java.util.Date();
            LocalDateTime dd = DateUtil.parseWithTime("01-01-2015", "dd-MM-yyyy");
            search.add(new SearchObjectCache(x, source, cat, text, dd, route));

        }

        List<SearchObjectCache> filtered = search.stream().filter(p -> p.getUrl().contains("tsaboin"))
                //.sorted()
                .collect(Collectors.toList());
        System.out.println(" ");
        System.out.format("%s,%s,%s,%s\n", TextUtil.format("ROUTE", 37), "STATUS", "DAY", "TIME");
        filtered.forEach(o -> System.out.format("%s,%s,%s,%s\n", TextUtil.format(o.getLocation(), 37), o.getCategory(), TextUtil.format(o.getDay(), 9), o.getTime()));

        //The search groups all SearchObjectCache by category:
        Map<String, List<SearchObjectCache>> byCategory = search.stream().collect(Collectors.groupingBy(o -> o.getLocation()));
        byCategory.forEach((cat, o) -> System.out.format("location %s: %s\n", cat, o));

        final Map<String, Map<String, Long>> byRoute = search.stream().collect(
                Collectors.groupingBy(SearchObjectCache::getCategory, Collectors.groupingBy(SearchObjectCache::getLocation, HashMap::new, Collectors.counting())));
        // bycategory.forEach((cat,i,o) -> System.out.format("%s\t| %s\t| %d\n", cat,i,o));

        System.out.println("\n" + TextUtil.format("Category", 15) + " | " + TextUtil.format("Route", 45) + " | Traffic");
        System.out.println("---------------------------------------------------------------------------------------");
        byRoute.forEach((m, c) -> c.forEach((m2, c2) -> System.out.println(TextUtil.format(m, 15) + " | " + TextUtil.format(m2, 45) + " | " + c2)));

        // System.out.println("\nObjectSummary by category");
        // Stream categorySummary = search.stream().collect(Collectors.sumarizingObject(p -> p.category));
        // System.out.println(categorySummary);
        List<String> al = Arrays.asList(new String[]{
            "This sample is by Steve from doublecloud.org, a leading ",
            "technical blog on virtualization, cloud computing, and ",
            "software architecture."});

        //int total = al.parallelStream().mapToInt(e -> e.split(" ").length).sum();
        //System.out.println("Total words:" + total+"\n");
    }

    public static void order() {

        Integer[] intArray = {1, 2, 3, 4, 5, 6, 7, 8};
        List<Integer> listOfIntegers = new ArrayList<>(Arrays.asList(intArray));

        System.out.println("listOfIntegers:");
        listOfIntegers.stream().forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("listOfIntegers sorted in reverse order:");
        Comparator<Integer> normal = Integer::compare;
        Comparator<Integer> reversed = normal.reversed();
        Collections.sort(listOfIntegers, reversed);

        listOfIntegers.stream().forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("Parallel stream");
        listOfIntegers.parallelStream().forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("Another parallel stream:");
        listOfIntegers.parallelStream().forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("With forEachOrdered:");
        listOfIntegers.parallelStream().forEachOrdered(e -> System.out.print(e + " "));
        System.out.println("");

        //Cities with customers doing expensive transactions
        /*java.util.Set<String> cities = transactions.stream()
                .filter(t -> t.getValue() > 1000)
                .map(Transaction::getCity)
                .collect(toSet());*/
    }

    //java -cp target\eduzi-core-1.0.jar;target\dependency\* com.ocularminds.eduzi.Analyser
    public static void main(String[] args) throws Exception {

        //java  -XX:+UseG1GC -XX:+UseStringDeduplication -cp .;target\eduzi-core-1.0.jar;target\dependency\* com.ocularminds.eduzi.Analyser
        //java  -XX:+UseG1GC -XX:+UseStringDeduplication -cp .;target\eduzi-core-1.0.jar;target\dependency\* com.ocularminds.eduzi.WebService
        //List<String> data = Arrays.asList("a1", "a2", "b1", "c2", "c1");
        //Analyser.analyse(data);
        //IntStream.range(1, 5).forEach(System.out::println);
        Analyser.reduce();
        // Analyser.process();
        //Analyser.order();
        // Analyser.createModel();
    }

}
