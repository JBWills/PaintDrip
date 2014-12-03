package edu.cmsc434.paintdrip.paintdripprototype.Paint;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evan on 12/1/14.
 */
public class Stroke {
    public List<LatLng> path = new ArrayList<LatLng>();
    public Style style = new Style();

    public Stroke() {
        style.thickness = 10;
        style.color = Color.BLACK;
    }

    public class Style {
        public int color;
        public int thickness;
    }

    public boolean isValid() {
        return path.size() >= 2;
    }
}
