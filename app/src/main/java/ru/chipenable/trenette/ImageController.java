package ru.chipenable.trenette;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.support.constraint.solver.widgets.Rectangle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.chipenable.trenette.data.IImageRepo;

/**
 * Created by Pavel.B on 26.08.2017.
 */

public class ImageController implements Wallpaper.FadeOutListener {

    private enum State {INIT, MOVE}

    private State state;
    private Point center;
    private Random random;
    private Paint paint;
    private Rectangle rect;
    private Paint rectPaint;
    private IImageRepo repo;
    private Coordinate endPoint;
    private Coordinate startPoint;
    private Looper looper;
    private int canvasWidth;
    private int canvasHeight;

    private List<Wallpaper> wallpaperList;
    private long lastTime;
    private double angles[] = {
            0,
            Math.toRadians(45),
            Math.toRadians(90),
            Math.toRadians(135),
            Math.toRadians(180),
            Math.toRadians(225),
            Math.toRadians(270),
            Math.toRadians(315),
    };

    public ImageController(IImageRepo repo, Looper looper) {
        this.repo = repo;
        this.looper = looper;

        state = State.INIT;
        random = new Random();
        paint = new Paint();
        paint.setColor(0xfa00ff00);
        rectPaint = new Paint();
        rectPaint.setColor(0xffff0000);
        wallpaperList = new ArrayList<>();
    }

    public int controller(Canvas canvas) {

        long curTime = System.currentTimeMillis();
        long deltaTime = curTime - lastTime;
        lastTime = curTime;

        switch (state) {
            case INIT: {
                int w = canvas.getWidth();
                int h = canvas.getHeight();

                /*center = new Point(500, 300);
                canvasWidth = 400;
                canvasHeight = 300;*/

                center = new Point(0, 0);
                canvasWidth = w;
                canvasHeight = h;

                createWallpaper(canvasWidth, canvasHeight)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.from(looper))
                        .subscribe(wallpaperList::add);

                state = State.MOVE;
                lastTime = System.currentTimeMillis();
                break;
            }

            case MOVE: {


                canvas.save();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                rectPaint.setStyle(Paint.Style.STROKE);

                for (Wallpaper w : wallpaperList) {
                    w.draw(canvas, deltaTime);
                }

                /*canvas.drawRect(center.x, center.y, center.x + canvasWidth, center.y + canvasHeight, rectPaint);
                canvas.drawRect(center.x - canvasWidth/2, center.y - canvasHeight/2,
                        center.x + canvasWidth/2, center.y + canvasHeight/2, rectPaint);*/

                canvas.restore();

                for (int i = 0; i < wallpaperList.size(); i++) {
                    Wallpaper w = wallpaperList.get(i);
                    if (w.isStopped()) {
                        wallpaperList.remove(i);
                    }
                }

                break;
            }
        }


        return 16;
    }

    @Override
    public void fadeOut() {
        createWallpaper(canvasWidth, canvasHeight)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.from(looper))
                .subscribe(wallpaperList::add);
    }

    private Observable<Wallpaper> createWallpaper(int canvasWidth, int canvasHeight) {
        return Observable.fromCallable(() -> {

            double angle = angles[random.nextInt(angles.length)];
            endPoint = calcIntersectionPoint(canvasWidth, canvasHeight, angle);
            startPoint = calcIntersectionPoint(canvasWidth, canvasHeight, angle + Math.PI);

            endPoint.x += center.x;
            endPoint.y += center.y;
            startPoint.x += center.x;
            startPoint.y += center.y;
            startPoint.x = startPoint.x > center.x? center.x:startPoint.x;
            startPoint.y = startPoint.y > center.y? center.y:startPoint.y;

            Bitmap image = repo.getRandomImage();
            image = Bitmap.createScaledBitmap(image, (3 * canvasWidth)/2, (3 * canvasHeight)/2, false);
            LineSegment path = new LineSegment(startPoint, endPoint);
            Wallpaper nextWallpaper = new Wallpaper(angle, image, path, 10000);
            nextWallpaper.setFadeOutListener(ImageController.this);

            return nextWallpaper;
        });
    }

    private Coordinate calcIntersectionPoint(double width, double height, double angle) {

        double lineLength = Math.sqrt(width * width + height * height);
        LineSegment testLine = new LineSegment(0, 0, Math.cos(angle) * lineLength, Math.sin(angle) * lineLength);
        LineSegment lineA = new LineSegment(-width / 2, -height / 2, width / 2, -height / 2);
        LineSegment lineB = new LineSegment(width / 2, -height / 2, width / 2, height / 2);
        LineSegment lineC = new LineSegment(width / 2, height / 2, -width / 2, height / 2);
        LineSegment lineD = new LineSegment(-width / 2, height / 2, -width / 2, -height / 2);

        LineSegment lineArray[] = {lineA, lineB, lineC, lineD};
        Coordinate p = null;

        for (LineSegment l : lineArray) {
            p = testLine.intersection(l);
            if (p != null) {
                break;
            }
        }

        return p == null ? new Coordinate(0, 0) : p;
    }


}
