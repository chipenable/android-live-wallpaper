package ru.chipenable.trenette.wallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.vividsolutions.jts.geom.LineSegment;

/**
 * Created by Pavel.B on 27.08.2017.
 */

public class Wallpaper {

    private enum State {MOVE, FADE_IN, FADE_OUT, STOPPED}

    private static final int MAX_ALPHA = 255;
    private static final long FADE_TIME = 2000;

    private Bitmap bitmap;
    private LineSegment path;
    private long liveTime;
    private double delta;
    private float alphaDelta;
    private float alpha;
    private double l;
    private double sinValue;
    private double cosValue;
    private Paint paint;
    private long time;
    private State state;
    private FadeOutListener fadeOutListener;

    public interface FadeOutListener{
        void fadeOut();
    }

    public Wallpaper(double angle, Bitmap bitmap, LineSegment path, long liveTime){
        this.bitmap = bitmap;
        this.path = path;
        this.liveTime = liveTime;

        double pathLength = path.getLength()/2;
        delta = pathLength/liveTime;
        alphaDelta = ((float)MAX_ALPHA)/FADE_TIME;
        alpha = 0;
        l = 0;

        sinValue = Math.sin(angle);
        cosValue = Math.cos(angle);
        paint = new Paint();
        paint.setColor(0xff00ff00);
        paint.setAlpha((int)alpha);

        state = State.FADE_IN;
    }

    public void setFadeOutListener(FadeOutListener listener){
        fadeOutListener = listener;
    }

    public void draw(Canvas canvas, int deltaTime){
        time += deltaTime;

        if (!isStopped()) {
            lifeCycle(time, deltaTime);
            double x = l * cosValue + path.p0.x;
            double y = l * sinValue + path.p0.y;
            canvas.drawBitmap(bitmap, (float)x, (float)y, paint);
            //canvas.drawLine((float)path.p0.x, (float)path.p0.y, (float)path.p1.x, (float)path.p1.y, paint);
            l += (delta * deltaTime);
        }

    }

    public void recycle(){
        bitmap.recycle();
        path = null;
        paint = null;
    }

    private void lifeCycle(long time, int deltaTime){

        switch(state){
            case FADE_IN:
                alpha += (alphaDelta * deltaTime);
                if (alpha > MAX_ALPHA){
                    alpha = MAX_ALPHA;
                    state = State.MOVE;
                }
                paint.setAlpha((int)alpha);
                break;

            case MOVE:
                if (time > liveTime - FADE_TIME){
                    state = State.FADE_OUT;
                    if (fadeOutListener != null){
                        fadeOutListener.fadeOut();
                    }
                }
                break;

            case FADE_OUT:
                alpha -= (alphaDelta * deltaTime);
                if (alpha < 0){
                    alpha = 0;
                    state = State.STOPPED;
                }
                paint.setAlpha((int)alpha);
                break;

            case STOPPED:
                break;
        }


    }


    public boolean isStopped(){
        return state == State.STOPPED;
    }


}
