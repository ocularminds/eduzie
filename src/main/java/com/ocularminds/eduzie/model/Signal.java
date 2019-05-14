package com.ocularminds.eduzie.model;

public class Signal {

    private VideoMessage data;
    private String to;

    public Signal() {
    }

    public VideoMessage getData() {
        return this.data;
    }

    public void setData(VideoMessage data) {
        this.data = data;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String toString() {
        return new String("").join("", "{\"to\":\"", to, "\",\"data\":\"", data.toString(), "\"}");
    }
}
