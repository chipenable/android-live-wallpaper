package ru.chipenable.trenette.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

import ru.chipenable.trenette.R;

/**
 * Created by Pavel.B on 25.08.2017.
 */

public class ImageRepo implements IImageRepo {

    private final Resources resources;
    private final Random random;
    private final int[] images = {
            R.drawable.carissa_gan_76325,
            R.drawable.eaters_collective_132772,
            R.drawable.eaters_collective_132773,
            R.drawable.jakub_kapusnak_296128
    };

    /*private final int[] images = {
            R.drawable.test_wallpaper
    };*/

    public ImageRepo(Resources resources){
        this.resources = resources;
        random = new Random();
    }

    @Override
    public Bitmap getRandomImage() {
        int imageId = images[random.nextInt(images.length)];
        return BitmapFactory.decodeResource(resources, imageId);
    }
}
