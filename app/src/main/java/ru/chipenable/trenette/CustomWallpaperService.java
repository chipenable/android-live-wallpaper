package ru.chipenable.trenette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class CustomWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ImageSetEngine();
    }

    private class ImageSetEngine extends Engine {

        public ImageSetEngine(){}

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawImage();
        }

        void drawImage() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_wallpaper_icon);
                    c.drawBitmap(b, 0, 0, new Paint());
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

        }

    }

}
