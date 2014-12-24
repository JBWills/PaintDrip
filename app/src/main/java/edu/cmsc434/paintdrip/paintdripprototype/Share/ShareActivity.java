package edu.cmsc434.paintdrip.paintdripprototype.Share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.facebook.AppEventsLogger;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.cmsc434.paintdrip.paintdripprototype.Feed.Painting;
import edu.cmsc434.paintdrip.paintdripprototype.ParseManager;
import edu.cmsc434.paintdrip.paintdripprototype.R;

public class ShareActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiLifecycleHelper uiHelper;
    private ToggleButton fbShareButton;
    private Button publishButton;
    private ParseManager parseManager;
    private Bitmap bitmap;
    private EditText description;

    private ToggleButton tumblrShareButton, twitterShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        parseManager = new ParseManager(getBaseContext());

        loadBitmapFromFile();

        ImageView imageView = (ImageView) findViewById(R.id.publish_preview_image);
        imageView.setImageBitmap(bitmap);

        fbShareButton = (ToggleButton) findViewById(R.id.fbShareButton);
        fbShareButton.setTextOff("");
        fbShareButton.setTextOn("");
        fbShareButton.getBackground().setAlpha(128);


        fbShareButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fbShareButton.getBackground().setAlpha(255);
                } else {
                    fbShareButton.getBackground().setAlpha(128);
                }
            }
        });

        publishButton = (Button) findViewById(R.id.publishButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishToFeed();
            }
        });

        tumblrShareButton = (ToggleButton) findViewById(R.id.tumblrShareButton);
        tumblrShareButton.getBackground().setAlpha(128);
       /* tumblrShareButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fbShareButton.getBackground().setAlpha(255);
                } else {
                    fbShareButton.getBackground().setAlpha(128);
                }
            }
        });*/

        twitterShareButton = (ToggleButton) findViewById(R.id.twitterShareButton);
        twitterShareButton.getBackground().setAlpha(128);
       /* twitterShareButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fbShareButton.getBackground().setAlpha(255);
                } else {
                    fbShareButton.getBackground().setAlpha(128);
                }
            }
        });
        */

        description = (EditText) findViewById(R.id.descriptionET);

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
    }

    private void loadBitmapFromFile() {
        FileInputStream in = null;
        try {
            in = openFileInput("currentPainting.png");
            bitmap = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishToFeed() {
        // save an image to parse with the bitmap, a description (to be set),
        // and a default number of likes
        String imageURL = parseManager.saveImage(bitmap,description.getText().toString(), 0);

        if(fbShareButton.isChecked()) {
            initiateFbShare(imageURL);
        }

        this.setResult(100, null);
        finish();
    }

    private void initiateFbShare(String imageURL) {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink(imageURL)
                .setPicture(imageURL)
                .setCaption(description.getText().toString())
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {
                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                                        Exception error, Bundle data) {
                        Log.e("Activity",
                                String.format("Error: %s", error.toString()));
                    }
                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Log.i("Activity", "Success!");
                    }
                });
    }

    @Override
    protected void onResume(){
        super.onResume();
        AppEventsLogger.activateApp(this);
        uiHelper.onResume();
        //setUpMapIfNeeded();
    }

    @Override
    protected void onPause(){
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        uiHelper.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
