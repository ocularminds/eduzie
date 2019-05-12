package com.ocularminds.eduzie;

public final class MediaFile {

    public Media audio;
    public Media video;

    public MediaFile(Media audio,Media video){

        this.audio = audio;
        this.video = video;
    }

    public static final class Media {

        public final String name;
        public final String type;
        public final String dataURL;

        public Media(String name,String type,String dataURL){

            this.name = name;
            this.type = type;
            this.dataURL = dataURL;
        }
    }
}