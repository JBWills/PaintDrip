package edu.cmsc434.paintdrip.paintdripprototype;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cmsc434.paintdrip.paintdripprototype.Paint.Painting;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.Stroke;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;
    private LocationListener mLocListener;

    private Painting painting;
    private List<Polyline> drawnPolylines;
    private boolean isPainting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        getActionBar().setTitle("Paint");
        mMap.getUiSettings().setZoomControlsEnabled(false);
        ToggleButton toggle = (ToggleButton)findViewById(R.id.button);
        toggle.setChecked(true);


        painting = new Painting();
        drawnPolylines = new LinkedList<Polyline>();

        mLocListener = new LocationListener() {
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
        mLocationManager.removeUpdates(mLocListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocListener);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void redrawPainting() {
        for (Polyline line : drawnPolylines) {
            line.remove();
        }
        drawnPolylines.clear();

        for (Stroke stroke : painting.getStrokes()) {
            Polyline drawnLine = mMap.addPolyline(
                    new PolylineOptions().addAll(stroke.path)
                                         .width(stroke.style.thickness)
                                         .color(stroke.style.color)
                                         .visible(true)
            );

            drawnPolylines.add(drawnLine);
        }
    }

    public void onPaintToggleClicked(View view) {
        if (isPainting) {
            painting.endStroke();
            isPainting = false;
        }
        else {
            isPainting = true;
        }
    }
}
