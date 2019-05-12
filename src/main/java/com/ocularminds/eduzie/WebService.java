package com.ocularminds.eduzie;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import java.util.List;
import java.util.Locale;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import spark.Spark;
import spark.Request;
import spark.utils.StringUtils;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.ocularminds.eduzie.vao.Feed;
import com.ocularminds.eduzie.common.FeedCache;
import com.ocularminds.eduzie.common.DateUtil;
import com.ocularminds.eduzie.common.FileUtil;
import com.ocularminds.eduzie.common.ImageUtil;
import com.ocularminds.eduzie.common.Passwords;
import com.ocularminds.eduzie.vao.Place;
import com.ocularminds.eduzie.vao.User;
import com.ocularminds.eduzie.vao.Comment;
import com.ocularminds.eduzie.vao.Message;
import com.ocularminds.eduzie.dao.Authorizer;
import com.ocularminds.eduzie.dao.PostWriter;
import com.ocularminds.eduzie.dao.DbFactory;

import org.apache.commons.beanutils.BeanUtils;
import javax.servlet.http.Part;
import javax.servlet.ServletOutputStream;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

public class WebService {

    FeedCache cache;
    Gson gson;
    Authorizer authorizer;
    PostWriter writer;

    final File upload = new File("upload");
    private static final String USER_SESSION_ID = "USER_SESSION_ID";
    MultipartConfigElement uploadConfig;

    public WebService() {

        cache = FeedCache.instance();
        gson = new Gson();
        authorizer = Authorizer.instance();
        writer = PostWriter.instance();
        DbFactory.instance();

        if (!upload.exists() && !upload.mkdirs()) {
            throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
        }
        uploadConfig = new MultipartConfigElement(upload.getAbsolutePath(), 1024 * 1024 * 5, 1024 * 1024 * 5 * 5, 1024 * 1024);
    }

    private void addAuthenticatedUser(Request request, User u) {
        request.session().attribute(USER_SESSION_ID, u);
    }

    private void removeAuthenticatedUser(Request request) {
        request.session().removeAttribute(USER_SESSION_ID);
    }

    private User getAuthenticatedUser(Request request) {
        return request.session().attribute(USER_SESSION_ID);
    }

    public static void main(String[] args) {

        WebService ws = new WebService();
        ws.listen();
        VideoService vs = new VideoService();
        vs.start();
    }

    //@todo: WebService - controller methods to be refactored using spring controllers
    //@todo: create sepration of concerns
    private void listen() {

        Spark.port(Integer.valueOf(System.getenv("PORT")));
        Spark.staticFileLocation("/public");
        Spark.get("/", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());

        Spark.get("/chat", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            return new ModelAndView(attributes, "chat.ftl");
        }, new FreeMarkerEngine());

        Spark.get("/ping", (request, response) -> {
            return new Fault("00", "Ok ready.");
        }, new JsonFront());

        Spark.post("/loader/push", (request, response) -> {

            Type listType = new TypeToken<ArrayList<SearchObjectCache>>() {
            }.getType();
            List<SearchObjectCache> data = gson.fromJson(request.body(), listType);

            System.out.println("push receives data size " + data.size());
            Fault fault = new Fault("00", "Success");

            if (data == null || data.isEmpty()) {
                fault = new Fault("51", "No data uploaded");
            }
            boolean isOperationSuccessful = cache.load(data);
            if (!isOperationSuccessful) {
                fault = new Fault("10", "Service not available. Try again");
            }

            fault.setData((Object) data);
            fault.setGroup("feed");
            //broadcast(fault);
            fault.setData(null);

            return fault;

        }, new JsonFront());

        Spark.get("/throwexception", (request, response) -> {
            throw new Exception();
        });

        exception(Exception.class, (e, request, response) -> {

            e.printStackTrace();
            response.status(404);
            response.body("Resource not found");
        });

        /*
	 * Displays the latest messages of all users.
         */
        Spark.get("/public", (req, res) -> {

            User user = getAuthenticatedUser(req);
            Map<String, Object> map = new HashMap<>();
            map.put("pageTitle", "Public Timeline");
            map.put("user", user);
            List<Message> messages = writer.findPublicTimelineMessages(user.getId());
            map.put("messages", messages);
            return new ModelAndView(map, "timeline.ftl");
        }, new FreeMarkerEngine());


        /*
	 * Displays the latest messages of all users.
         */
        Spark.get("/video", (req, res) -> {

            User user = getAuthenticatedUser(req);
            Map<String, Object> map = new HashMap<>();
            map.put("pageTitle", "Public Timeline");
            return new ModelAndView(map, "video.ftl");
        }, new FreeMarkerEngine());


        /*
 * Presents the login form or redirect the user to
 * her timeline if it's already logged in
         */
        Spark.get("/screen", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            User user = getAuthenticatedUser(req);
            if (user == null) {
                res.redirect("/");
                halt();
            } else {

                List<Message> messages = writer.findPostForUser(user);
                Message message = new Message();
                message.setTitle("Good " + DateUtil.timeOfDay() + ", " + user.getName().split("\\s+")[0]);
                message.setTime(DateUtil.tommorow());
                message.setType(SearchPlace.nextWeather("Lagos,NG"));
                message.setText("7.50 and 8.00");
                message.setPlace("Ikeja-Maryland");
                map.put("timelines", messages);
                map.put("message", message);
                map.put("email", user.getEmail());
                map.put("user", user);
                map.put("username", user.getName().split("\\s+")[0]);

            }

            return new ModelAndView(map, "screen.ftl");
        }, new FreeMarkerEngine());
        /*
  	 * Logs the user in.
         */
        Spark.post("/login", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            User user = new User();
            try {
                MultiMap<String> params = new MultiMap<String>();
                UrlEncoded.decodeTo(req.body(), params, "UTF-8");
                BeanUtils.populate(user, params);

            } catch (Exception e) {
                halt(501);
                return null;
            }

            Fault fault = authorizer.checkUser(user);
            String page = "";
            if (fault.getData() != null) {

                addAuthenticatedUser(req, (User) fault.getData());
                res.redirect("/screen");

            } else {

                map.put("error", fault.getError());
                map.put("email", user.getEmail());
                page = "index.ftl";
            }

            return new ModelAndView(map, page);
        }, new FreeMarkerEngine());

        /*
  	 * Presents the register form or redirect the user to
  	 * her timeline if it's already logged in
         */
        Spark.get("/register", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "index.ftl");
        }, new FreeMarkerEngine());
        /*
  	 * Registers the user.
         */
        Spark.post("/register", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            User user = new User();
            String page = "index.ftl";

            try {

                MultiMap<String> params = new MultiMap<String>();
                UrlEncoded.decodeTo(req.body(), params, "UTF-8");
                BeanUtils.populate(user, params);

                String error = user.validate();
                if (StringUtils.isEmpty(error)) {

                    User existingUser = authorizer.findByUserName(user.getEmail());
                    if (existingUser == null) {

                        user.setPassword(Passwords.hashPassword(user.getPassword()));
                        authorizer.save(user);
                        page = "screen.ftl";
                        user = authorizer.findByUserName(user.getEmail());
                        map.put("user", user);
                        List<Message> messages = writer.findPostForUser(user);
                        Message message = new Message();
                        message.setTitle("Good " + DateUtil.timeOfDay() + ", " + user.getName().split("\\s+")[0]);
                        message.setTime(DateUtil.tommorow());
                        message.setType(SearchPlace.nextWeather("Lagos,NG"));
                        message.setText("7.50 and 8.00");
                        message.setPlace("Ikeja-Maryland");
                        map.put("timelines", messages);
                        map.put("message", message);
                        map.put("email", user.getEmail());
                        map.put("user", user);
                        map.put("username", user.getName().split("\\s+")[0]);

                    } else {

                        error = "The username is already taken";
                        page = "index.ftl";
                    }
                }

                map.put("createError", error);
                map.put("name", user.getName());
                map.put("email", user.getEmail());

            } catch (Exception e) {

                e.printStackTrace();
                halt(501);
                return null;
            }

            return new ModelAndView(map, page);
        }, new FreeMarkerEngine());
        /*
  	 * Checks if the user is already authenticated
         */
        Spark.before("/register", (req, res) -> {
            User authUser = getAuthenticatedUser(req);
            if (authUser != null) {
                res.redirect("/screen");
                halt();
            }
        });

        /*
  	 * Logs the user out and redirects to the public timeline
         */
        Spark.get("/logout", (req, res) -> {
            removeAuthenticatedUser(req);
            res.redirect("/public");
            halt();
            return null;
        });

        /*
	 * Displays a user's tweets.
         */
        Spark.get("/api/move/message/:username", (req, res) -> {

            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);

            User authUser = getAuthenticatedUser(req);
            boolean followed = false;
            if (authUser != null) {
                followed = authorizer.isFollowing(authUser.getId(), profileUser.getId());
            }

            List<Message> messages = writer.findPostForUser(profileUser);
            Map<String, Object> map = new HashMap<>();
            map.put("pageTitle", username + "'s Timeline");
            map.put("user", authUser);
            map.put("profileUser", profileUser);
            map.put("followed", followed);
            map.put("timelines", messages);
            return new ModelAndView(map, "timeline.ftl");
        }, new FreeMarkerEngine());
        /*
	 * Checks if the user exists
         */
        Spark.before("/api/move/user/:username", (req, res) -> {

            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);
            if (profileUser == null) {
                halt(404, "User not Found");
            }
        });


        /*
	 * Adds the current user as follower of the given user.
         */
        Spark.get("/api/move/user/follow/:username", (req, res) -> {

            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            authorizer.follow(authUser.getId(), profileUser.getId());
            res.redirect("/api/move/user/" + username);
            return null;
        });
        /*
	 * Checks if the user is authenticated and the user to follow exists
         */
        Spark.before("/api/move/user/:username/follow", (req, res) -> {
            String username = req.params(":username");
            User authUser = getAuthenticatedUser(req);
            User profileUser = authorizer.findByUserName(username);
            if (authUser == null) {
                res.redirect("/");
                halt();
            } else if (profileUser == null) {
                halt(404, "User not Found");
            }
        });


        /*
	 * Removes the current user as follower of the given user.
         */
        Spark.get("/api/move/user/:username/unfollow", (req, res) -> {
            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            authorizer.unfollow(authUser.getId(), profileUser.getId());
            res.redirect("/api/move/user/" + username);
            return null;
        });
        /*
	 * Checks if the user is authenticated and the user to unfollow exists
         */
        Spark.before("/api/move/user/:username/unfollow", (req, res) -> {

            String username = req.params(":username");
            User authUser = getAuthenticatedUser(req);
            User profileUser = authorizer.findByUserName(username);
            if (authUser == null) {
                res.redirect("/");
                halt();
            } else if (profileUser == null) {
                halt(404, "User not Found");
            }
        });

        /*
	 * comments on a given message by a given user.
         */
        Spark.post("/api/move/message/comment/:username/:id", (req, res) -> {

            String username = req.params(":username");
            String messageid = req.params(":id");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            Comment comment = new Comment();
            MultiMap<String> params = new MultiMap<String>();
            UrlEncoded.decodeTo(req.body(), params, "UTF-8");
            BeanUtils.populate(comment, params);

            writer.comment(messageid, comment, profileUser.getId());
            res.redirect("/screen");
            return null;

        });

        /*
	 * uncomments on a given message by a given user.
         */
        Spark.get("/api/move/message/uncomment/:username/:id", (req, res) -> {

            String username = req.params(":username");
            String commentid = req.params(":id");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            writer.uncomment(commentid);
            res.redirect("/screen");
            return null;

        });

        /*
	 * attedn the current event by given user.
         */
        Spark.get("/api/move/message/attend/:username/:id", (req, res) -> {

            String username = req.params(":username");
            String messageid = req.params(":id");

            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            writer.attend(messageid, profileUser.getId());
            res.redirect("/screen");
            return null;
        });


        /*
	 * updates the current message by the given user.
         */
        Spark.get("/api/move/message/edit/:username/:id", (req, res) -> {

            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            authorizer.unfollow(authUser.getId(), profileUser.getId());
            res.redirect("/screen");
            return null;
        });


        /*
	 * Removes the current message by the given user.
         */
        Spark.get("/api/move/message/delete/:username/:id", (req, res) -> {
            String username = req.params(":username");
            User profileUser = authorizer.findByUserName(username);
            User authUser = getAuthenticatedUser(req);

            authorizer.unfollow(authUser.getId(), profileUser.getId());
            res.redirect("/screen");
            return null;
        });

        /*
	 * posts a new message for the user.
         */
        Spark.post("/api/move/message/new/:username", (req, res) -> {

            String image = null;
            String ext = null;
            String dir = null;
            String photo = null;
            try {

                req.raw().setAttribute("org.eclipse.jetty.multipartConfig", uploadConfig);
                Part uploaded = req.raw().getPart("file"); //file is name of the upload form
                ext = FileUtil.extension(FileUtil.name(uploaded));
                image = FileUtil.nextText();
                photo = image + "." + ext;
                dir = upload.getAbsolutePath();
                InputStream in = uploaded.getInputStream();
                Files.copy(in, new File(upload.getAbsolutePath() + File.separator + photo).toPath());

            } catch (javax.servlet.ServletException s) {

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            //User user = getAuthenticatedUser(req);
            String email = req.params(":username");
            User user = authorizer.findByUserName(email);

            String title = req.raw().getParameter("title");
            String type = req.raw().getParameter("type");
            String text = req.raw().getParameter("text");
            String place = req.raw().getParameter("place");
            String date = req.raw().getParameter("date");
            String time = req.raw().getParameter("time");
            String imageFor = req.raw().getParameter("image_for");
            if ((imageFor != null) && (imageFor.equals("profile")) && (photo != null)) {

                String pic = image + "50." + ext;
                String avatar = image + "110." + ext;
                ImageUtil.scale(dir + File.separator + photo, dir + File.separator + avatar, ImageUtil.AVATAR_WIDTH, ImageUtil.AVATAR_HEIGHT, ext);
                ImageUtil.scale(dir + File.separator + photo, dir + File.separator + pic, ImageUtil.AVATAR_SMALL_WIDTH, ImageUtil.AVATAR_SMALL_HEIGHT, ext);

                user.setPic(pic);
                user.setAvatar(avatar);
                authorizer.save(user);

            } else if ((imageFor != null) && (imageFor.equals("cover")) && (photo != null)) {

                user.setCoverImage(photo);
                authorizer.save(user);
            }

            Fault fault = new Fault("00", "Success");
            Message m = new Message();
            m.setPublished(new java.util.Date());

            m.setAuthor(user);
            m.setPhoto(photo);
            m.setType(type);
            m.setTitle(title);
            m.setText(text);
            m.setPlace(place);
            m.setTime(time);
            writer.write(m);

            res.status(200);
            res.redirect("/screen");
            return fault;

        }, new JsonFront());

        Spark.get("/api/move/photo/:id", (request, response) -> {

            String fileName = request.params(":id");
            byte[] photo = FileUtil.loadFromDisk("upload" + File.separator + fileName);

            response.type("image/png");
            //response.body(data);
            ServletOutputStream os = response.raw().getOutputStream();
            os.write(photo);
            os.flush();

            response.status(200);
            return "";

        });//,new JsonFront());

        Spark.get("/api/move/locate/:longitude/:latitude/:range", (request, response) -> {

            String longitude = request.params(":longitude");
            String latitude = request.params(":latitude");
            String distance = request.params(":range");

            List<String> all = new ArrayList();
            all.add("Festus, 200");
            all.add("Tolu all");
            response.status(200);
            response.type("application/json");
            return all;

        }, new JsonFront());

        Spark.get("/api/move/feeds", (request, response) -> {

            List<Feed> feeds = cache.findAll();//new ArrayList<SearchObjectCache>();
            if (feeds.size() == 0) {

                java.util.Date dd = new java.util.Date();
                String ds = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm", Locale.US).format(dd);
                feeds.add(new Feed(new Long(1), "Any eduzi.movement", dd, "Learn. Anticipate. Move ", "TODO", "i", "", "#"));
            }
            response.status(200);
            response.type("application/json");
            return feeds;

        }, new JsonFront());

        Spark.post("/api/move/location", (request, response) -> {

            Fault fault = new Fault("00", "Success");
            Map<String, String> m = gson.fromJson(request.body(), Map.class);
            String userid = m.get("userid");
            String longitude = m.get("lon");
            String latitude = m.get("lat");

            System.out.println("updating user " + userid + " location longitude:" + longitude + ",latitude:" + latitude);

            String name = null;//"Oshodi Lagos Nigeria Lagos Lagos Nigeria Apapa Lagos Nigeria Ikorodu Lagos Nigeria Oworonsoki Ojota Ikeja Agege Ogba Somolu OjuElegba Berger Ojodu ";//"Oshodi";
            String type = null;//"neighborhood|political|routes|point_of_interest";
            String distance = "500";
            String s = SearchPlace.search(latitude, longitude, distance, type, name);
            List<Place> places = SearchPlace.parsePlaces(latitude, longitude, s);
            System.out.println("total Places found - " + places.size());

            for (int x = 0; x < places.size(); x++) {

                String d = "";
                String w = "";
                double r = places.get(x).getDistance();
                if (r == 0.00) {
                    continue;
                }

                if (places.get(x).getDistance() < 1) {

                    d = String.format("%.2fm", r * 1000);
                    w = String.format("%2dmins walk", SearchPlace.nextArrival(r, SearchPlace.WALK_MODE));

                } else {

                    double t = SearchPlace.nextArrival(r, SearchPlace.DRIVE_MODE);
                    d = String.format("%.2fkm", r);
                    if (t < 1) {
                        w = String.format("%.2fmins drive", t * 60);
                    } else {
                        w = String.format("%.2fhrs drive", t);
                    }
                }

                System.out.println("You are " + String.format("%.4f", r) + " " + d + " from " + places.get(x).getName() + " " + w);
            }
            return fault;

        }, new JsonFront());

    }

}
