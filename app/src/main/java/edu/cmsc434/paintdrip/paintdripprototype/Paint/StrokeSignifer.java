package edu.cmsc434.paintdrip.paintdripprototype.Paint;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import edu.cmsc434.paintdrip.paintdripprototype.R;

public class StrokeSignifer extends View {

    public StrokeSignifer(Context context) {
        super(context);
        init(null, 0);
    }

    public StrokeSignifer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StrokeSignifer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Resources r = getResources();
        Paint paintStroke = new Paint();
        paintStroke.setAntiAlias(true);
        paintStroke.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()));
        paintStroke.setStyle(Paint.Style.STROKE);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int color = prefs.getInt("color_2", 0xFF000000);
        paintStroke.setColor(color);
        float length = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
        canvas.drawLine(0,0, length, 0, paintStroke);
    }
}
