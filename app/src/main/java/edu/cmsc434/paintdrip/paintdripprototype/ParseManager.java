package edu.cmsc434.paintdrip.paintdripprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.nio.ByteBuffer;

/**
 * Created by nadeem on 12/12/14.
 */
public class ParseManager {
    public ParseManager() {

    }

    // getHomeFeed(String userId) returns a list of Paintings
    // likePainting(String userId, String paintingId)
    // boolean isPaintingLiked(String userId, String paintingId)

    // saves an image file to Parse
    // imageFileName = "meal_photo.jpg"
    public static void saveImage(String imgFileName, final Activity activity, Bitmap img) {

        // copy image to a buffer to get the byte data
        int numBytes = img.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(numBytes);
        img.copyPixelsToBuffer(buf);

        byte[] imgData = buf.array();

        // save the image to Parse
        ParseFile photoFile = new ParseFile(imgFileName, imgData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(activity,
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    /* saved */
                    Toast.makeText(activity, "saved", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
