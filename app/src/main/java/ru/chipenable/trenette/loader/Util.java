package ru.chipenable.trenette.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class Util {

    private static final String TAG = "Util";

    public static boolean saveBitmapAsFile(Context context, Bitmap bitmap, String fileName) {
        File appDirectory = context.getFilesDir();

        /** if file exists app deletes it and creates new one*/
        File imgFile = context.getFileStreamPath(fileName);
        if (imgFile.exists()){
            imgFile.delete();
        }
        imgFile = new File(appDirectory, fileName);

        /** write bitmat to file */
        FileOutputStream outputStream = null;
        try  {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
            return false;
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception: ", e);
                }
            }
        }

        imgFile = context.getFileStreamPath(fileName);
        return imgFile.exists();
    }

    public static boolean isUrlValid(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static String parseImageName(String url){
        String imgName = "";

        if (TextUtils.isEmpty(url)){
            return imgName;
        }

        int nameStartIndex = url.lastIndexOf('/');
        if (nameStartIndex == -1){
            return imgName;
        }

        return url.substring(nameStartIndex + 1, url.length());
    }

    public static File[] getImgFiles(File dir){
        File[] emptyFileList = new File[0];
        if (dir == null){
            return emptyFileList;
        }

        File[] result = dir.listFiles(file -> {
            String name = file.getName();
            return name.matches("([^\\s]+(\\.(?i)(jpg|png|bmp)))");
        });

        if (result == null){
            return emptyFileList;
        }
        return result;
    }


}
