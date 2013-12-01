package org.alabs.nolotiro;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.alabs.nolotiro.exceptions.NolotiroException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// This task checks if the Ad photo is available in cache and downloads it if necessary
public class LoadThumbnailTask extends AsyncTask<Ad, Void, Bitmap> {

    private static final String TAG = "LoadThumbnailTask";
    private NolotiroAPI api;
    private ImageView image;
    private Activity activity;

    public LoadThumbnailTask(Activity _activity, ImageView _image) {
        api = NolotiroAPI.getInstance();
        activity = _activity;
        image = _image;
    }

    protected void onPreExecute() {

    }

    protected void onPostExecute(final Bitmap bitmap) {
        // Set image and hide progress animation
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //ProgressBar progress = (ProgressBar) activity.findViewById(R.id.progressBar);
                //progress.setVisibility(View.GONE);
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                } else {
                    image.setImageResource(R.drawable.no_logo);
                }
            }
        });
    }

    // Retrieve photo from cache dir if available. Otherwise download it.
    protected Bitmap doInBackground(Ad... ads) {
        Ad ad = ads[0];
        Bitmap bitmap = null;
        boolean isInternet;
        File f;

        // No photo available
        if (ad.getImageFilename() == null) {
            Log.w(TAG, "photo is null, no thumbnail");
            return null;
        }

        try {
            f = Utils.getThumbnailPath(activity, ad);

            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            } else {
                isInternet = Utils.isInternetAvailable(activity);

                if (!isInternet) {
                    return null;
                } else {
                    URL url = api.getThumbnailUrlFromAd(ad);
                    // If there's no photo then don't download it
                    if (url == null) {
                        return null;
                    }

                    Log.i(TAG, "Downloading image file " + url.toString());
                    BufferedInputStream bis = new BufferedInputStream(url.openStream());
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f.getAbsolutePath());

                    while ((read = bis.read(bytes)) != -1) {
                        fo.write(bytes, 0, read);
                    }
                    fo.close();

                    Log.i(TAG, "File saved to: " + f.getAbsolutePath());
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                }
            }
        } catch (NolotiroException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
