package edu.cmsc434.paintdrip.paintdripprototype;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.PaintingPath;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.Stroke;
import edu.cmsc434.paintdrip.paintdripprototype.Share.ShareActivity;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener  {
    private final int DEFAULT_ZOOM_LEVEL = 18;

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    private PaintingPath painting;
    private List<Polyline> drawnPolylines;

    private boolean isPainting = false;
    private Tool selectedTool = Tool.NONE;
    private boolean isSaving = false;

    private enum Tool {
        ERASER, PENCIL, PAINTBRUSH, PEN, NONE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slider.setPanelSlideListener(new StyleSlideListener());

        SlidingUpPanelLayout saveSlider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_save);
        saveSlider.setPanelSlideListener(new SaveSlideListener());

        painting = new PaintingPath();
        painting.setColor(getSelectedColor());
        drawnPolylines = new LinkedList<Polyline>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        if (googleApiClient.isConnected()) {
            fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (googleApiClient.isConnected()) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    public void onBackPressed() {
        if (isSaving) {
            SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_save);
            slider.setSlidingEnabled(true);
            slider.collapsePanel();
            isSaving = false;
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
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
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        centerMapAtCurrentLocation();
    }

    //region Map Functions
    private void centerMapAtCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM_LEVEL));
        }
    }
    //endregion

    //region Layout Functions

    private class StyleSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View view, float v) {
            ImageView arrow = (ImageView)findViewById(R.id.drawer_arrow);
            if (v > .5) {
                arrow.setRotation(180);
            }
            else {
                arrow.setRotation(0);
            }
        }

        @Override
        public void onPanelCollapsed(View view) {
        }

        @Override
        public void onPanelExpanded(View view) {
        }

        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {

        }
    }

    private class SaveSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View view, float v) {
        }

        @Override
        public void onPanelCollapsed(View view) {
        }

        @Override
        public void onPanelExpanded(View view) {
            SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_save);
            slider.setSlidingEnabled(false);
        }

        @Override
        public void onPanelAnchored(View view) {
            SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_save);
            slider.setSlidingEnabled(false);
        }

        @Override
        public void onPanelHidden(View view) {

        }
    }

    //endregion
    //region Event Functions

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_save);
            slider.setSlidingEnabled(true);
            slider.expandPanel();
            isSaving = true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFrameConfirm(View view) {
        map.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                Bitmap croppedPainting = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                FileOutputStream out = null;
                try {
                    out = openFileOutput("currentPainting.png", Context.MODE_PRIVATE);
                    croppedPainting.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                startActivityForResult(new Intent(MapsActivity.this, ShareActivity.class), 69);

            }
        });
    }
    //endregion

    //region Painting Functions
    public void onEraserClicked(View view) {
        if (selectedTool != Tool.ERASER) {
            minimizeTool(getViewFromTool(selectedTool));
            selectedTool = Tool.ERASER;
            maximizeTool(view);
            paintingOff();
        }
        else {
            selectedTool = Tool.NONE;
            minimizeTool(view);
            paintingOff();
        }
    }

    public void onPencilClicked(View view) {
        if (selectedTool != Tool.PENCIL) {
            minimizeTool(getViewFromTool(selectedTool));
            selectedTool = Tool.PENCIL;
            maximizeTool(view);
            paintingOn();
        }
        else {
            selectedTool = Tool.NONE;
            minimizeTool(view);
            paintingOff();
        }
    }

    public void onPaintbrushClicked(View view) {
        if (selectedTool != Tool.PAINTBRUSH) {
            minimizeTool(getViewFromTool(selectedTool));
            selectedTool = Tool.PAINTBRUSH;
            maximizeTool(view);
            paintingOn();
        }
        else {
            selectedTool = Tool.NONE;
            minimizeTool(view);
            paintingOff();
        }
    }

    public void onPenClicked(View view) {
        if (selectedTool != Tool.PEN) {
            minimizeTool(getViewFromTool(selectedTool));
            selectedTool = Tool.PEN;
            maximizeTool(view);
            paintingOn();
        }
        else {
            minimizeTool(view);
            selectedTool = Tool.NONE;
            paintingOff();
        }
    }

    private View getViewFromTool(Tool tool) {
        switch (tool){
            case ERASER:
                return findViewById(R.id.drawer_eraser);
            case PENCIL:
                return findViewById(R.id.drawer_pencil);
            case PAINTBRUSH:
                return findViewById(R.id.drawer_paintbrush);
            case PEN:
                return findViewById(R.id.drawer_pen);
            default:
                return null;
        }
    }

    private void minimizeTool(View tool){
        if (tool != null) {
            Resources r = getResources();
            float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -30, r.getDisplayMetrics());
            TranslateAnimation anim = new TranslateAnimation(0, 0, offset, 0);
            anim.setDuration(250);
            anim.setFillAfter(true);
            tool.startAnimation(anim);
        }
    }

    private void maximizeTool(View tool){
                if (tool != null) {
                    Resources r = getResources();
                    float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -30, r.getDisplayMetrics());
                    TranslateAnimation anim = new TranslateAnimation(0, 0, 0, offset);
                    anim.setDuration(250);
            anim.setFillAfter(true);
            tool.startAnimation(anim);
        }
    }

    public void paintingOff() {
        if (isPainting) {
            painting.endStroke();
            isPainting = false;
        }
    }

    public void paintingOn() {
        if (!isPainting) {
            isPainting = true;
        }
    }

    private void redrawPainting() {
        for (Polyline line : drawnPolylines) {
            line.remove();
        }
        drawnPolylines.clear();

        for (Stroke stroke : painting.getStrokes()) {
            Polyline drawnLine = map.addPolyline(
                    new PolylineOptions().addAll(stroke.path)
                                         .width(stroke.style.thickness)
                                         .color(stroke.style.color)
                                         .visible(true)
            );

            drawnPolylines.add(drawnLine);
        }
    }
    //endregion

    //region Color Picker Functions
    public void onInkwellClicked(View view) {
        //The color picker menu item as been clicked. Show
        //a dialog using the custom ColorPickerDialog class.

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialValue = prefs.getInt("color_2", 0xFF000000);

        System.out.println("INITIAL COLOR: " + initialValue);

        Log.d("mColorPicker", "initial value:" + initialValue);

        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);

        //colorDialog.setAlphaSliderVisible(true);
        colorDialog.setTitle("Pick a Color");

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save the value in our preferences.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("color_2", colorDialog.getColor());
                editor.commit();
                painting.setColor(colorDialog.getColor());
                findViewById(R.id.drawer_inkwell).invalidate();
            }
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing to do here.
            }
        });

        colorDialog.show();
    }

    private int getSelectedColor() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return prefs.getInt("color_2", 0xFF000000);
    }

    private String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }
    //endregion


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 69 && resultCode == 100){
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("PLAYSERVICES", "Connected");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getBaseContext(), "Failed to connect to Google Play Services", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(), "Failed to connect to Google Play Services", Toast.LENGTH_LONG).show();
    }

    public void onLocationChanged(Location l) {
        Log.e("LLEVENT", "You moved to " + l);
        if (isPainting)
            painting.addPointToStroke(new LatLng(l.getLatitude(), l.getLongitude()));
        redrawPainting();
    }
}
