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
import android.widget.Button;

import edu.cmsc434.paintdrip.paintdripprototype.R;


/**
 * TODO: document your custom view class.
 */
public class InkwellButton extends Button {
    public InkwellButton(Context context) {
        super(context);
    }

    public InkwellButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InkwellButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint inkPaint = new Paint();
        inkPaint.setAntiAlias(true);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int color = prefs.getInt("color_2", 0xFF000000);
        inkPaint.setColor(color);
        Resources r = getResources();
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, inkPaint);
    }
}
