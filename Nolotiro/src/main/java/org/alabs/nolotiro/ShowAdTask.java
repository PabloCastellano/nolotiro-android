package org.alabs.nolotiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.alabs.nolotiro.db.DbAdapter;
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
public class ShowAdTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "ShowAdTask";
    private Fragment fragment;
    private Context context;
    private NolotiroAPI api;
    private ImageView image;
    private String errorMessage = null;
    private Ad ad;

    public ShowAdTask(Fragment _fragment, Ad _ad) {
        api = NolotiroAPI.getInstance();
        context = _fragment.getActivity();
        fragment = _fragment;
        ad = _ad;
        Log.i(TAG, "ad=" + ad);
    }

    protected void onPreExecute() {
        TextView title = (TextView)fragment.getActivity().findViewById(R.id.textTitle);
        TextView description = (TextView)fragment.getActivity().findViewById(R.id.textDescription);

        image = (ImageView)fragment.getActivity().findViewById(R.id.imageImage);
        image.setVisibility(View.INVISIBLE);
        title.setText(ad.getTitle());
        description.setText(ad.getBody());
        fragment.getActivity().setTitle(ad.getTitle());
    }

    protected void onPostExecute(final Bitmap bitmap) {
        final String finalMessage = errorMessage;

        // Set image and hide progress animation
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                ProgressBar progress = (ProgressBar)fragment.getActivity().findViewById(R.id.progressBar);
                progress.setVisibility(View.GONE);
                if(bitmap != null) {
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(bitmap);
                } else if (errorMessage != null) {
                    Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                }
                // otherwise the ad doesn't contain any photo
            }
        });
    }

    // Retrieve photo from cache dir if available. Otherwise download it.
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        String photoPath = null;
        boolean isInternet;
        File f;

        // No photo available
        if (ad.getImageFilename() == null) {
            Log.w(TAG, "photo is null");
            return null;
        }

        try {
            String nolotiroDir = Utils.getNolotiroCacheDir(context);
            f = new File(nolotiroDir);
            if (!f.exists()) {
                Log.i(TAG, "Mkdir " + f);
                f.mkdirs();
            }

            f = new File(nolotiroDir + ad.getImageFilename());

            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(f.toString());
            } else {
                isInternet = Utils.isInternetAvailable(context);

                if (!isInternet) {
                    errorMessage = context.getResources().getString(R.string.error_connecting);
                    return null;
                } else {
                    URL url = api.getPhotoUrlFromAd(ad);
                    // If there's no photo then don't download it
                    if (url == null) {
                        return null;
                    }

                    Log.i("Nolotiro", "Downloading image file " + url.toString());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                    bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f.getAbsoluteFile());
                    fo.write(bytes.toByteArray());
                    fo.close();
                    Log.i(TAG, "File saved to: " + f.getAbsolutePath());
                }
            }
            photoPath = f.getAbsolutePath();

        } catch (NolotiroException e) {
            e.printStackTrace();
            errorMessage = context.getResources().getString(R.string.error_retrieving_ads);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            errorMessage = context.getResources().getString(R.string.error_retrieving_ads);
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = context.getResources().getString(R.string.error_retrieving_ads);
        }

        image.setTag(photoPath);
        return bitmap;
    }


}
