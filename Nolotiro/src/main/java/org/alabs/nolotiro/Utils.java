package org.alabs.nolotiro;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.alabs.nolotiro.exceptions.NolotiroException;

import java.io.File;


public class Utils {

    private static final String TAG = "NolotiroUtils";
    private static String NOLOTIRO_DIR = "Nolotiro";

    //public String getExternalNolotiroDir() {
    /*
    public String getNolotiroDir() throws NolotiroException {
        String state = Environment.getExternalStorageState();
        String dir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = Environment.getExternalStorageDirectory() + File.separator + NOLOTIRO_DIR;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            dir = getFilesDir() + File.separator + NOLOTIRO_DIR;
            Log.i(TAG, "External storage is read only. Using internal memory.");
            Log.i(TAG, "Internal storage directory is " + dir);
        } else {
            Log.w(TAG, "media error: state=" + state);
            throw new NolotiroException("Error trying to get Nolotiro directory");
        }

        return dir;
    }
    */

    public static String getNolotiroCacheDir(Context ctx) throws NolotiroException {
        String state = Environment.getExternalStorageState();
        String dir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = ctx.getExternalCacheDir().toString();
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            dir = ctx.getCacheDir().toString();
            Log.i(TAG, "External cache storage is read only. Using internal memory.");
            Log.i(TAG, "Internal cache storage directory is " + dir);
        } else {
            Log.w(TAG, "media error: state=" + state);
            throw new NolotiroException("Error trying to get Nolotiro cache directory");
        }

        dir += File.separator + NOLOTIRO_DIR;
        return dir;
    }

    public static String getNolotiroDir(Context ctx) throws NolotiroException {
        String state = Environment.getExternalStorageState();
        String dir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            dir = ctx.getFilesDir().toString();
            Log.i(TAG, "External storage is read only. Using internal memory.");
            Log.i(TAG, "Internal storage directory is " + dir);
        } else {
            Log.w(TAG, "media error: state=" + state);
            throw new NolotiroException("Error trying to get Nolotiro directory");
        }

        dir += File.separator + NOLOTIRO_DIR;
        return dir;
    }

}
