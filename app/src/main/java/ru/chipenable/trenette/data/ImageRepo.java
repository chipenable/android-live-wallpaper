package ru.chipenable.trenette.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.chipenable.trenette.R;

/**
 * Created by Pavel.B on 25.08.2017.
 */

public class ImageRepo implements IImageRepo {

    private final Random random;
    private List<IImageKeeper> images;

    public ImageRepo(Resources resources){
        random = new Random();
        images = new ArrayList<>();

        int[] localImageIds = {
                R.drawable.carissa_gan_76325,
                R.drawable.eaters_collective_132772,
                R.drawable.eaters_collective_132773,
                R.drawable.jakub_kapusnak_296128
        };

        for(int imageId: localImageIds){
            images.add(new InternalImage(resources, imageId));
        }
    }

    @Override
    public Bitmap getRandomImage() {
        int randomIndex = random.nextInt(images.size());
        IImageKeeper imageKeeper = images.get(randomIndex);
        return imageKeeper.getImage();
    }

}
