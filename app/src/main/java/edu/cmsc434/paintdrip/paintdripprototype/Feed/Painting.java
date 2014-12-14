package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Painting")
public class Painting extends ParseObject {

    public String username;
    public String description;
    public int likes;
    public Bitmap image;

    public Painting() {
        // default constructor (required for Parse)
    }

    // Dummy constructor - used to upload a dummy painting
    public Painting(String username, String description, int likes, Bitmap image) {
        this.username = username;
        this.description = description;
        this.likes = likes;
        this.image = image;
    }

    public Painting(String authorId, String username, String description, int numLikes, ParseFile imgFile) {
        setUsername(username);
        setDescription(description);
        setLikesCount(numLikes);
        setPhotoFile(imgFile);
        setAuthorId(authorId);
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

    public void setLikesCount(int likes) {
        put("likesCount", likes);
    }

    //
    public void likePhoto() {
        ParseUser user = ParseUser.getCurrentUser();

        List<String> likedPaintings = user.getList("likedPaintings");
        if(likedPaintings == null) {
            likedPaintings = new ArrayList<String>();
        }
        likedPaintings.add(this.getObjectId());

        user.put("likedPaintings", likedPaintings);
        user.saveInBackground();

        int likesCount = getInt("likesCount");
        put("likesCount", ++likesCount);
    }

    public void setAuthorId(String authorId) {
        put("authorId", authorId);
    }

    public String getAuthorId() {
        return getString("authorId");
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public String getUsername() {
        return getString("username");
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public boolean isLiked() {
        ParseUser user = ParseUser.getCurrentUser();

        List<ParseObject> likedPaintings = user.getList("likedPaintings");
        if(likedPaintings == null) {
            return false;
        }

        // this makes checking for all likes O(n^2)... to be improved
        if(likedPaintings.contains(this.getObjectId())) {
            return true;
        }else {
            return false;
        }
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
