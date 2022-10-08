package com.baontq.mob201.model;

public class News {
    private String title;
    private String description;
    private String previewImage;
    private String link;

    public News(String title, String description, String previewImage, String link) {
        this.title = title;
        this.description = description;
        this.previewImage = previewImage;
        this.link = link;
    }

    public News() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return title;
    }
}
