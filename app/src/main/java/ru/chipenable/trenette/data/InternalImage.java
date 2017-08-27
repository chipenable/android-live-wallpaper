package ru.chipenable.trenette.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

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

}
