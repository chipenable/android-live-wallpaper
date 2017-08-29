package ru.chipenable.trenette.wallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.inject.Inject;

import ru.chipenable.trenette.Trenette;

public class CustomWallpaperService extends WallpaperService {

    private final String TAG = getClass().getName();

    @Inject
    WallpaperController imageController;

    @Override
    public void onCreate() {
        super.onCreate();
        Trenette app = (Trenette)getApplication();
        app.getAppComponent().inject(this);
    }

    @Override
    public Engine onCreateEngine() {
        return new ImageSetEngine(imageController);
    }

    private class ImageSetEngine extends Engine {

        private final Runnable imageTask;
        private final Handler handler;
        private final WallpaperController controller;

        public ImageSetEngine(WallpaperController controller){
            this.controller = controller;
            handler = new Handler();
            imageTask = this::drawImage;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                handler.post(imageTask);
            } else {
                handler.removeCallbacks(imageTask);
            }
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            handler.removeCallbacks(imageTask);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(imageTask);
        }

        void drawImage() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = null;
            int delay = -1;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    delay = controller.drawWallpaper(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            if (delay >= 0) {
                handler.postDelayed(imageTask, delay);
            }

        }

    }

}
