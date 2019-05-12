package com.ocularminds.eduzie.vao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "edz_feed", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"fid"})})
public class Feed implements java.io.Serializable {

    private static final long serialVersionUID = 8881008581492811600L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fid", nullable = false, unique = true, length = 11)
    private Long id;

    @Column(name = "cat", nullable = true, length = 18)
    private String category;

    @Column(name = "time", nullable = true)
    private Date time;

    @Column(name = "text", nullable = true)
    private String text;

    @Column(name = "name", nullable = false, length = 26)
    private String name;

    @Column(name = "icon", nullable = true, length = 26)
    private String icon;

    @Column(name = "image", nullable = true, length = 75)
    private String image;

    @Column(name = "url", nullable = true, length = 75)
    private String url;

    //@todo: remove explicit caall to override method in constructor
    public Feed(Long id, String category, Date time, String text, String name, String icon, String image, String url) {

        setId(id);
        setCategory(category);
        setTime(time);
        setText(text);
        setName(name);
        setIcon(icon);
        setImage(image);
        setUrl(url);

    }

    public Feed() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        return (id.intValue() * 17) * name.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o instanceof Feed) {
            return ((Feed) o).getId().equals(this.getId());
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "," + time + "," + text + "," + name + "," + icon + "," + image + "," + url;
    }
}
