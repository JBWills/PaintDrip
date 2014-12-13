package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.graphics.Bitmap;

/**
 * Created by jamesbwills on 12/11/14.
 */
public class Painting {

    private String username;
    private String description;
    private int likes;
    private Bitmap image;

    public Painting(String username, String description, int likes, Bitmap image) {
        this.username = username;
        this.description = description;
        this.likes = likes;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public int getLikes() {
        return likes;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
