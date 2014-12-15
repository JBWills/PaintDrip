package edu.cmsc434.paintdrip.paintdripprototype.Paint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by evan on 12/1/14.
 */
public class PaintingPath  {
    private List<Stroke> strokes;
    private Stroke currentStroke;

    public PaintingPath() {
        strokes = new LinkedList<Stroke>();
        currentStroke = new Stroke();
    }

    // All strokes returned by this function are valid
    public List<Stroke> getStrokes() {
        List returnStrokes = new LinkedList<Stroke>(strokes);
        if (currentStroke.isValid())
           returnStrokes.add(currentStroke);

        return returnStrokes;
    }

    public void addPointToStroke(LatLng point) {
        currentStroke.path.add(point);
    }

    public void endStroke() {
        if (currentStroke.isValid()) {
            strokes.add(currentStroke);
            currentStroke = new Stroke();
        }
    }

    public void setColor(int color) {
        if (currentStroke.isValid()) {
            LatLng lastPoint = currentStroke.path.get(currentStroke.path.size() - 1);
            endStroke();
            addPointToStroke(lastPoint);
        }
        else {
            endStroke();
        }
        currentStroke.style.color = color;
    }


}
