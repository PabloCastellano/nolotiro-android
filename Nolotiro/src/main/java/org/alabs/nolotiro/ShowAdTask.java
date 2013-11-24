package org.alabs.nolotiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.alabs.nolotiro.exceptions.NolotiroException;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

// This task checks if the Ad photo is available in cache and downloads it if necessary
public class ShowAdTask extends AsyncTask<Integer, Void, Bitmap> {

    private static final String TAG = "ShowAdTask";
    private Fragment fragment;
    private Context context;
    private NolotiroAPI api;
    private ImageView image;
    private Integer itemId = 0;
    private Exception exception = null;

    public ShowAdTask(Fragment _fragment) {
        api = NolotiroAPI.getInstance();
        context = _fragment.getActivity();
        fragment = _fragment;
    }

    protected void onPreExecute() {
        image = (ImageView)fragment.getActivity().findViewById(R.id.imageImage);
        image.setVisibility(View.INVISIBLE);
    }

    protected void onPostExecute(final Bitmap bitmap) {
        String message = null;

        // Handle doInBackground exception
        if (exception != null) {
            exception.printStackTrace();

            if (exception instanceof IOException) {
                message = context.getResources().getString(R.string.error_connecting);
            } else if (exception instanceof JSONException) {
                message = context.getResources().getString(R.string.error_retrieving_ad);
            }
            Log.e(TAG, message);
        }
        final String finalMessage = message;

        // Set image and hide progress animation
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                ProgressBar progress = (ProgressBar)fragment.getActivity().findViewById(R.id.progressBar);
                progress.setVisibility(View.GONE);
                if(bitmap != null) {
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(bitmap);
                } else {
                    if (exception != null) {
                        Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    protected Bitmap doInBackground(Integer... itemIds) {
        itemId = itemIds[0];
        Bitmap bitmap = null;
        File f = null;
        Ad ad = null;
        try {
            ad = api.getAd(itemId);
        } catch (Exception e) {
            ad = null;
            exception = e;
        }

        if (ad == null)
            return null;

        final Ad finalAd = ad;

        // TODO: Retrieve from DB
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                TextView title = (TextView)fragment.getActivity().findViewById(R.id.textTitle);
                TextView description = (TextView)fragment.getActivity().findViewById(R.id.textDescription);
                title.setText(finalAd.getTitle());
                description.setText(finalAd.getBody());
                fragment.getActivity().setTitle(finalAd.getTitle());
            }
        });

        if(ad.getImageFilename() == null)
            return null;

        try {
            String nolotiroDir = Utils.getNolotiroCacheDir(context);
            f = new File(nolotiroDir);
            if (!f.exists()) {
                Log.i(TAG, "Mkdir " + f);
                f.mkdirs();
            }

            f = new File(nolotiroDir + ad.getImageFilename());
            Log.i("ShowAdTask", f.toString());

            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(f.toString());
            } else {
                URL url = new URL(api.getPhotoUrlFromAd(ad));
                Log.i("Nolotiro", "Downloading image file " + url.toString());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                if(bitmap == null) {
                    Log.e("Nolotiro", "Bitmap es null");
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f.getAbsoluteFile());
                fo.write(bytes.toByteArray());
                fo.close();
                Log.i(TAG, "File saved to: " + f.getAbsolutePath());
            }
        } catch (NolotiroException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.setTag(f.getAbsolutePath());

        return bitmap;
    }
}
