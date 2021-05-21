package com.capstone.streetefficient.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DrawingView extends View {
    public static final float TOUCH_TOLERANCE = 10;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    private int w;
    private int h;


    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    private void init() {
        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(7);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paintLine);
        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchedMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchedMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);


                //Calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                //if the distance is significant enough to be considered a movement
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    //move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    //store the new coordinated
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId);//get the corresponding path
        bitmapCanvas.drawPath(path, paintLine);//draw to bitmap canvass
        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path;
        Point point;

        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        } else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    public void clear() {
        pathMap.clear();//removes all of the path
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();//refresh Screen
    }

    public void setImage(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            bitmap.eraseColor(Color.BLACK);
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            bitmapCanvas.drawBitmap(image, (w - image.getWidth()) / 2, (h - image.getHeight()) / 2, paintLine);

        }
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
