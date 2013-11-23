package org.alabs.nolotiro;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.alabs.nolotiro.exceptions.NolotiroException;

import java.io.File;


public class Utils {

    private static final String TAG = "NolotiroUtils";
    private static String NOLOTIRO_DIR = "Nolotiro";

    public static String getNolotiroCacheDir(Context ctx) throws NolotiroException {
        String state = Environment.getExternalStorageState();
        String dir = null;
        File f = ctx.getExternalCacheDir();

        if (Environment.MEDIA_MOUNTED.equals(state) && f != null) {
            dir = f.toString();
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            f = ctx.getCacheDir();
            if (f != null) {
                dir = f.toString();
                Log.i(TAG, "External cache storage is read only. Using internal memory.");
                Log.i(TAG, "Internal cache storage directory is " + dir);
            } else {
                Log.w(TAG, "media error: state=" + state);
                throw new NolotiroException("Error trying to get Nolotiro cache directory");
            }
        }

        dir += File.separator + NOLOTIRO_DIR + File.separator;
        return dir;
    }

    public static String getNolotiroDir(Context ctx) throws NolotiroException {
        String state = Environment.getExternalStorageState();
        String dir = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File f = ctx.getFilesDir();
            if (f != null) {
                dir = f.toString();
                Log.i(TAG, "External storage is read only. Using internal memory.");
                Log.i(TAG, "Internal storage directory is " + dir);
            } else {
                Log.w(TAG, "media error: state=" + state);
                throw new NolotiroException("Error trying to get Nolotiro directory");
            }
        }

        dir += File.separator + NOLOTIRO_DIR + File.separator;
        return dir;
    }

}
