package edu.cmsc434.paintdrip.paintdripprototype;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.LinkedList;
import java.util.List;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.Painting;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.Stroke;
import edu.cmsc434.paintdrip.paintdripprototype.Share.ShareActivity;


public class MapsActivity extends FragmentActivity {
    private final int DEFAULT_ZOOM_LEVEL = 18;

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private LocationListener locationListener;

    private Painting painting;
    private List<Polyline> drawnPolylines;
    private boolean isPainting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        getActionBar().setTitle("Paint");
        map.getUiSettings().setZoomControlsEnabled(false);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getMyLocation();

        SlidingUpPanelLayout slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slider.setPanelSlideListener(new StyleSlideListener());

        ToggleButton toggle = (ToggleButton)findViewById(R.id.button);
        toggle.setChecked(true);

        painting = new Painting();
        drawnPolylines = new LinkedList<Polyline>();

        locationListener = new LocationListener() {
            private final String TAG = "LLEvent";

            public void onLocationChanged(Location l) {
                Log.e(TAG, "You moved to " + l);
                if (isPainting)
                    painting.addPointToStroke(new LatLng(l.getLatitude(), l.getLongitude()));
                redrawPainting();
            }

            public void onProviderEnabled(String p) {
                Log.i(TAG, "Provider enabled");
            }

            public void onProviderDisabled(String p) {
                Log.i(TAG, "Provider disabled");
            }

            public void onStatusChanged(String p, int status, Bundle extras) {
                Log.i(TAG, "Status changed");
            }
        };
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
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
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
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(DEFAULT_ZOOM_LEVEL)   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    //endregion
    //region Event Functions

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            startActivity(new Intent(this, ShareActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Painting Functions
    public void onPaintToggleClicked(View view) {
        if (isPainting) {
            painting.endStroke();
            isPainting = false;
        }
        else {
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
    public void onShowColorPickerClicked(View view) {
        //The color picker menu item as been clicked. Show
        //a dialog using the custom ColorPickerDialog class.

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialValue = prefs.getInt("color_2", 0xFF000000);

        System.out.println("INITIAL COLOR: " + initialValue);

        Log.d("mColorPicker", "initial value:" + initialValue);

        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);

        colorDialog.setAlphaSliderVisible(true);
        colorDialog.setTitle("Pick a Color!");

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "Selected Color: " + colorToHexString(colorDialog.getColor()), Toast.LENGTH_LONG).show();

                //Save the value in our preferences.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("color_2", colorDialog.getColor());
                editor.commit();
                painting.setColor(colorDialog.getColor());
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

    private String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }
    //endregion
}
