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

    public Painting(String authorId, String username, String description, int numLikes, ParseFile imgFile) {
        setUsername(username);
        setDescription(description);
        setLikesCount(numLikes);
        setPhotoFile(imgFile);
        setAuthorId(authorId);
    }

    // Dummy constructor - used to upload a dummy painting
    public Painting(String username, String description, int likes, Bitmap image) {
        this.username = username;
        this.description = description;
        this.likes = likes;
        this.image = image;
    }

    // ---------------------
    // Dummy methods - used for dummy paintings
    // ---------------------
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    // ---------------------

    public int getLikesCount() {
        return getInt("likesCount");
    }

    public void setLikesCount(int likes) {
        put("likesCount", likes);
    }

    // add this painting to the users list of likedPaintings and increment like count
    public void likePainting() {
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

    // remove this painting from the users list of likedPaintings and decrement like count
    public void unlikePainting() {
        ParseUser user = ParseUser.getCurrentUser();

        List<String> likedPaintings = user.getList("likedPaintings");
        if(likedPaintings == null) {
            return;
        }
        likedPaintings.remove(this.getObjectId());

        user.put("likedPaintings", likedPaintings);
        user.saveInBackground();

        int likesCount = getInt("likesCount");
        put("likesCount", --likesCount);
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

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }
}
