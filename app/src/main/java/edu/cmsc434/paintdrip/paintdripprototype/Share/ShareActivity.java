package edu.cmsc434.paintdrip.paintdripprototype.Share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.cmsc434.paintdrip.paintdripprototype.R;

public class ShareActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiLifecycleHelper uiHelper;
    private Button fbShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        fbShareButton = (Button) findViewById(R.id.fbShareButton);
        fbShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateFbShare();
            }
        });

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        setUpMapIfNeeded();
    }

    private void initiateFbShare() {
        // TODO Auto-generated method stub
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink("https://developers.facebook.com/android")
                .setPicture(
                        "http://m.c.lnkd.licdn.com/mpr/mpr/p/4/005/052/10a/0e7ce98.jpg")
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
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause(){
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        uiHelper.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
