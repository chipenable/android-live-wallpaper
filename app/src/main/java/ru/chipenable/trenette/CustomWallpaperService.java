package ru.chipenable.trenette;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import ru.chipenable.trenette.data.IImageRepo;
import ru.chipenable.trenette.data.ImageRepo;

public class CustomWallpaperService extends WallpaperService {

    private IImageRepo imageRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        imageRepo = new ImageRepo(getApplicationContext().getResources());
    }

    @Override
    public Engine onCreateEngine() {
        return new ImageSetEngine();
    }

    private class ImageSetEngine extends Engine {

        private final Runnable imageTask;
        private final Handler handler;
        private final ImageController imageController;

        public ImageSetEngine(){
            imageController = new ImageController(imageRepo, getMainLooper());
            handler = new Handler();
            imageTask = this::drawImage;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawImage();
        }

        void drawImage() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = null;
            int delay = 0;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    delay = imageController.controller(canvas);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }

            if (delay >= 0) {
                handler.postDelayed(imageTask, delay);
            }

        }

    }

}
