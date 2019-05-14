package com.ocularminds.eduzie.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "posts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pid"})})
public class Post implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid", nullable = false, unique = true, length = 11)
    private Long id;

    @Column(name = "title", length = 150, nullable = true)
    private String title;

    @Column(name = "msg_type", length = 18, nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "author", nullable = true)
    private User author;

    @Column(name = "text", length = 250, nullable = true)
    private String text;

    @Column(name = "place", length = 35, nullable = true)
    private String place;

    @Column(name = "time", length = 10, nullable = true)
    private String time;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "date_posted", nullable = true)
    private final Date date;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "pub_dt", nullable = true)
    private Date published;

    @Column(name = "published", length = 35, nullable = true)
    private String publishedStr;

    @Column(name = "photo", length = 35, nullable = true)
    private String photo;

    @OneToMany
    @JoinTable(name = "edz_event_attendees",
            joinColumns = @JoinColumn(name = "mid"),
            inverseJoinColumns = @JoinColumn(name = "uid")
    )
    private Set<User> attendees;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Photo> photos;

    public Post() {

        attendees = new HashSet<>();
        comments = new ArrayList<>();
        photos = new ArrayList<>();
        published = new Date();
        date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public Set<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<User> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(User user) {
        this.attendees.add(user);
    }

    public List<Comment> getComments() {

        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {

        comments.add(comment);
        comment.setMessage(this);
    }

    public List<Photo> getPhotos() {

        if (photos == null) {
            photos = new ArrayList<>();
        }
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {

        photos.add(photo);
        photo.setMessage(this);
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getPublishedStr() {
        return publishedStr;
    }

    public void setPublishedStr(String publishedStr) {
        this.publishedStr = publishedStr;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
