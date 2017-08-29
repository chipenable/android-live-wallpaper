package ru.chipenable.trenette.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import ru.chipenable.trenette.di.RxSchedulers;
import ru.chipenable.trenette.repo.IImageRepo;

/**
 * Created by Pavel.B on 26.08.2017.
 */

public class WallpaperController implements Wallpaper.FadeOutListener {

    private enum State {INIT, MOVE}

    public static final int LIVE_TIME = 10000;
    public static final int IMG_QUALITY = 50;
    private static final int UPDATE_PERIOD = 16;
    private static final int SCALE_FACTOR = 3;
    private final String TAG = getClass().getName();

    private State state;
    private Point center;
    private Random random;
    private Paint rectPaint;
    private IImageRepo repo;
    private int canvasWidth;
    private int canvasHeight;
    private RxSchedulers schedulers;
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

    public WallpaperController(IImageRepo repo, RxSchedulers schedulers) {
        this.repo = repo;
        this.schedulers = schedulers;

        state = State.INIT;
        random = new Random();
        rectPaint = new Paint();
        rectPaint.setColor(0xffff0000);
        wallpaperList = new ArrayList<>();
    }

    public int drawWallpaper(Canvas canvas) {

        long curTime = System.currentTimeMillis();
        long deltaTime = curTime - lastTime;
        lastTime = curTime;

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        switch (state) {
            case INIT: {

                //for debugging
                /*center = new Point(300, 200);
                canvasWidth = 500;
                canvasHeight = 300;*/

                center = new Point(0, 0);

                createWallpaper(canvasWidth, canvasHeight)
                        .subscribeOn(schedulers.getCompScheduler())
                        .observeOn(schedulers.getUiScheduler())
                        .subscribe(wallpaperList::add);

                state = State.MOVE;
                break;
            }

            case MOVE: {

                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                rectPaint.setStyle(Paint.Style.STROKE);

                for (Wallpaper w : wallpaperList) {
                    w.draw(canvas, (int)deltaTime);
                }

                //for debugging
                /*canvas.drawRect(center.x, center.y, center.x + canvasWidth, center.y + canvasHeight, rectPaint);
                canvas.drawRect(center.x - canvasWidth/3, center.y - canvasHeight/3,
                        center.x + canvasWidth/3, center.y + canvasHeight/3, rectPaint);*/

                for (int i = 0; i < wallpaperList.size(); i++) {
                    Wallpaper w = wallpaperList.get(i);
                    if (w.isStopped()) {
                        w.recycle();
                        wallpaperList.remove(i);
                    }
                }

                break;
            }
        }

        long error = deltaTime > UPDATE_PERIOD? deltaTime - UPDATE_PERIOD:0;
        return error > UPDATE_PERIOD? UPDATE_PERIOD: UPDATE_PERIOD - (int)error;
    }

    @Override
    public void fadeOut() {
        createWallpaper(canvasWidth, canvasHeight)
                .subscribeOn(schedulers.getCompScheduler())
                .observeOn(schedulers.getUiScheduler())
                .subscribe(wallpaperList::add);
    }

    private Observable<Wallpaper> createWallpaper(int canvasWidth, int canvasHeight) {
        return Observable.fromCallable(() -> {

            int w = canvasWidth/SCALE_FACTOR;
            int h = canvasHeight/SCALE_FACTOR;

            double angle = angles[random.nextInt(angles.length)];
            Coordinate endPoint = calcIntersectionPoint(w, h, angle);
            Coordinate startPoint = calcIntersectionPoint(w, h, angle + Math.PI);

            endPoint.x += center.x;
            endPoint.y += center.y;
            startPoint.x += center.x;
            startPoint.y += center.y;
            startPoint.x = startPoint.x > center.x? center.x:startPoint.x;
            startPoint.y = startPoint.y > center.y? center.y:startPoint.y;

            //scale image - image must be (SCALE_FACTOR + 1) times bigger than canvas
            Bitmap originalBitmap = repo.getRandomImage();
            float imageWidth = originalBitmap.getWidth();
            float imageHeight = originalBitmap.getHeight();
            float xScaleFactor = ((SCALE_FACTOR + 1) * w)/imageWidth;
            float yScaleFactor = ((SCALE_FACTOR + 1) * h)/imageHeight;
            float scaleFactor = Math.max(xScaleFactor, yScaleFactor);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, (int)(imageWidth * scaleFactor),
                    (int)(imageHeight * scaleFactor), false);
            originalBitmap.recycle();

            Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    w*(SCALE_FACTOR + 1), h*(SCALE_FACTOR + 1));
            scaledBitmap.recycle();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY, out);
            croppedBitmap.recycle();

            Bitmap optimisedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            LineSegment path = new LineSegment(startPoint, endPoint);
            Wallpaper nextWallpaper = new Wallpaper(angle, optimisedBitmap, path, LIVE_TIME);
            nextWallpaper.setFadeOutListener(WallpaperController.this);

            return nextWallpaper;
        });
    }

    private Coordinate calcIntersectionPoint(double width, double height, double angle) {

        double lineLength = Math.sqrt(width * width + height * height);
        LineSegment testLine = new LineSegment(0, 0, Math.cos(angle) * lineLength, Math.sin(angle) * lineLength);
        LineSegment lineA = new LineSegment(-width, -height, width, -height);
        LineSegment lineB = new LineSegment(width, -height, width, height);
        LineSegment lineC = new LineSegment(width, height, -width, height);
        LineSegment lineD = new LineSegment(-width, height, -width, -height);

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
