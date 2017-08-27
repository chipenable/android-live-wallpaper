package ru.chipenable.trenette;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.vividsolutions.jts.geom.Coordinate;
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
    private double l;
    private double pathLength;
    private double sinValue;
    private double cosValue;
    private Paint paint;
    private long time;
    private long fadeTimeThreshold;
    private State state;
    private Coordinate curPoint;
    private FadeOutListener fadeOutListener;

    public interface FadeOutListener{
        void fadeOut();
    }

    public Wallpaper(double angle, Bitmap bitmap, LineSegment path, long liveTime){
        this.bitmap = bitmap;
        this.path = path;
        this.liveTime = liveTime;

        pathLength = path.getLength()/2;
        delta = pathLength/liveTime;
        l = 0;

        sinValue = Math.sin(angle);
        cosValue = Math.cos(angle);
        paint = new Paint();
        paint.setColor(0xff00ff00);
        paint.setAlpha(0);

        fadeTimeThreshold = liveTime - FADE_TIME;
        state = State.FADE_IN;
        curPoint = new Coordinate(path.p0.x, path.p0.y);
    }

    public void setFadeOutListener(FadeOutListener listener){
        fadeOutListener = listener;
    }

    public void draw(Canvas canvas, long deltaTime){
        time += deltaTime;

        if (!isStopped()) {

            double x = l * cosValue + path.p0.x;
            double y = l * sinValue + path.p0.y;
            canvas.drawBitmap(bitmap, (float)x, (float)y, paint);
            //canvas.drawLine((float)path.p0.x, (float)path.p0.y, (float)path.p1.x, (float)path.p1.y, paint);
            l += (delta * deltaTime);

            fade(time, deltaTime);
        }

    }

    private void fade(long time, long deltaTime){
        int alpha;

        switch(state){
            case FADE_IN:
                alpha = paint.getAlpha();
                alpha += (MAX_ALPHA/(FADE_TIME/deltaTime));
                alpha = alpha > MAX_ALPHA? MAX_ALPHA : alpha;
                paint.setAlpha(alpha);

                if (alpha == MAX_ALPHA){
                    state = State.MOVE;
                }
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
                alpha = paint.getAlpha();
                alpha -= (MAX_ALPHA/(FADE_TIME/deltaTime));
                alpha = alpha < 0? 0 : alpha;
                paint.setAlpha(alpha);

                if (alpha == 0){
                    state = State.STOPPED;
                }
                break;

            case STOPPED:
                break;
        }


    }


    public boolean isStopped(){
        return state == State.STOPPED;
    }


}
