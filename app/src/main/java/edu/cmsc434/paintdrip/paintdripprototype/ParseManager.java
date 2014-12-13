package edu.cmsc434.paintdrip.paintdripprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

/**
 * Created by nadeem on 12/12/14.
 */
public class ParseManager {


    // saves an image file to Parse
    // imageFileName = "meal_photo.jpg"
    public static void saveImage(String imgFileName, final Activity activity, Bitmap img) {
        // Save the scaled image to Parse
        ParseFile photoFile = new ParseFile(imgFileName, scaledData);
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
