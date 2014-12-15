package edu.cmsc434.paintdrip.paintdripprototype;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import edu.cmsc434.paintdrip.paintdripprototype.Feed.FeedItemDummy;
import edu.cmsc434.paintdrip.paintdripprototype.Feed.Painting;

/**
 * Created by nadeem on 12/12/14.
 */

public class ParseManager {
    Context context;

    public ParseManager(Context context) {
        this.context = context;
    }

    public void uploadDummyImages() {
        FeedItemDummy dummyGenerator = new FeedItemDummy(context);
        List<Painting> paintings = dummyGenerator.getGlobalOrFeedItems(5);
        System.out.println("Paintings count: " + paintings.size());

        for(Painting p : paintings) {
            saveImage(p, p.image);
        }
    }

    public String saveImage(Bitmap img, String description, int defaultNumLikes) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null) {
            System.out.println("ParseManager.java USER NOT LOGGED IN. NO CURRENT USER. COULD NOT SAVE");
            return null;
        }

        // convert bitmap to png and save as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgData = stream.toByteArray();
        final ParseFile photoFile = new ParseFile("painting.png", imgData);

        try {
            photoFile.save();
        }catch(ParseException e) {
            e.printStackTrace();
        }
        // upload the painting
        final Painting parseUploadedPainting = new Painting(currentUser.getObjectId(),
                currentUser.getUsername(), description, defaultNumLikes, photoFile);
        updateUser(parseUploadedPainting);

        return photoFile.getUrl();
    }


    public String saveImage(final Painting painting, Bitmap img) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null) {
            System.out.println("ParseManager.java USER NOT LOGGED IN. NO CURRENT USER. COULD NOT SAVE");
            return null;
        }

        // convert bitmap to png and save as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgData = stream.toByteArray();
        final ParseFile photoFile = new ParseFile("painting.png", imgData);

        // upload the painting
        final Painting parseUploadedPainting = new Painting(currentUser.getObjectId(),
                currentUser.getUsername(), painting.description, painting.likes, photoFile);
        updateUser(parseUploadedPainting);

        return photoFile.getUrl();
    }

    // Save the painting object and add this painting as a reference ot the user's list of paintings
    public void updateUser(Painting parseUploadedPainting) {
        ParseUser user = ParseUser.getCurrentUser();

        List<ParseObject> list = user.getList("paintings");
        if(list != null) {
            list.add(parseUploadedPainting);
        }else {
            list = new ArrayList<ParseObject>();
            list.add(parseUploadedPainting);
        }

        user.put("paintings", list);
        user.saveInBackground();
    }
}
