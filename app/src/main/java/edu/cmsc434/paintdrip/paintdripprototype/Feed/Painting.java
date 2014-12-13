package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.graphics.Bitmap;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jamesbwills on 12/11/14.
 */
public class Painting extends ParseObject {

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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    /* Parse Methods */
    public int getLikesCount() {
        return getInt("likesCount");
    }

    public void likePhoto() {
        int likesCount = getInt("likesCount");
        put("likesCount", ++likesCount);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public String getRating(String rating) {
        return getString("rating");
    }

    public void setRating(String rating) {
        put("rating", rating);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }


}
