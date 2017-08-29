package ru.chipenable.trenette.repo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.chipenable.trenette.R;
import ru.chipenable.trenette.loader.ImgLoader;
import ru.chipenable.trenette.loader.Util;
import ru.chipenable.trenette.repo.imgkeeper.ExternalImage;
import ru.chipenable.trenette.repo.imgkeeper.IImageKeeper;
import ru.chipenable.trenette.repo.imgkeeper.InternalImage;

/**
 * Created by Pavel.B on 25.08.2017.
 */

/**
 * class that provides images from local and remote sources
 */
public class ImageRepo implements IImageRepo {

    private final String TAG = getClass().getName();
    private final Random random;
    private List<IImageKeeper> images;
    private File imagesDir;

    public ImageRepo(ImgLoader loader, Context context) {

        random = new Random();
        images = new ArrayList<>();
        imagesDir = context.getFilesDir();

        int[] localImageIds = {
                R.drawable.carissa_gan_76325,
                R.drawable.eaters_collective_132772,
                R.drawable.eaters_collective_132773,
                R.drawable.jakub_kapusnak_296128
        };

        //add local images to repo
        Resources resources = context.getResources();
        for (int imageId : localImageIds) {
            images.add(new InternalImage(resources, imageId));
        }

        //subscribe to loader for adding downloaded images
        loader.subscribeToEvents()
                .subscribe(
                        event -> {
                            File[] listFiles = Util.getImgFiles(imagesDir);
                            for (File f : listFiles) {
                                ExternalImage extImg = new ExternalImage(f.getAbsolutePath());
                                if (!images.contains(extImg)) {
                                    images.add(extImg);
                                }
                            }
                        },
                        throwable -> Log.d(TAG, "exception: " + throwable),
                        () -> {}
                );

        loader.startLoaderPeriodically()
                .subscribe(
                        aBoolean -> {},
                        throwable -> {},
                        () -> {}
                );
    }

    @Override
    public Bitmap getRandomImage() {
        int randomIndex = random.nextInt(images.size());
        IImageKeeper imageKeeper = images.get(randomIndex);
        return imageKeeper.getImage();
    }

}
