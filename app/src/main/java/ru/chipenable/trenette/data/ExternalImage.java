package ru.chipenable.trenette.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class ExternalImage implements IImageKeeper {

    private final String imagePath;

    public ExternalImage(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public Bitmap getImage() {
        return BitmapFactory.decodeFile(imagePath);
    }
}
