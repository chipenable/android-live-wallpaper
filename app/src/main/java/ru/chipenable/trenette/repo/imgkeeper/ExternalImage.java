package ru.chipenable.trenette.repo.imgkeeper;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExternalImage that = (ExternalImage) o;

        return imagePath != null ? imagePath.equals(that.imagePath) : that.imagePath == null;

    }

    @Override
    public int hashCode() {
        return imagePath != null ? imagePath.hashCode() : 0;
    }
}
