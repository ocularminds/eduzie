package com.ocularminds.eduzie.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "edz_comments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cmid"})})
public class Comment implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmid", nullable = false, unique = true, length = 11)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mid", insertable = true, updatable = true, nullable = false)
    Post message;

    @ManyToOne
    @JoinColumn(name = "author", nullable = true)
    private User author;

    @Column(name = "text", length = 250, nullable = true)
    private String text;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "event_dt", nullable = true)
    private Date published;

    @Column(name = "published", length = 35, nullable = true)
    private String publishedStr;

    @Column(name = "avatar", length = 35, nullable = true)
    private String gravatar;

    public Comment() {
        published = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getMessage() {
        return message;
    }

    public void setMessage(Post message) {
        this.message = message;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getGravatar() {
        return gravatar;
    }

    public void setGravatar(String gravatar) {
        this.gravatar = gravatar;
    }
}
