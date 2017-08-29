package ru.chipenable.trenette.repo.imgkeeper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

import ru.chipenable.trenette.repo.imgkeeper.IImageKeeper;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class InternalImage implements IImageKeeper {

    private final Resources resources;
    private final int imageId;

    public InternalImage(Resources resources, @DrawableRes int imageId){
        this.resources = resources;
        this.imageId = imageId;
    }

    @Override
    public Bitmap getImage() {
        return BitmapFactory.decodeResource(resources, imageId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InternalImage that = (InternalImage) o;

        return imageId == that.imageId;

    }

    @Override
    public int hashCode() {
        return imageId;
    }
}
