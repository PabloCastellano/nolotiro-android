package org.alabs.nolotiro;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.alabs.nolotiro.exceptions.NolotiroException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

// This task checks if the Ad photo is available in cache and downloads it if necessary
public class ShowAdTask extends AsyncTask<Integer, Void, ShowAdTask.AdWithBitmap> {

    public class AdWithBitmap {
        Ad ad;
        Bitmap bitmap;

        public AdWithBitmap(Ad ad, Bitmap bitmap) {
            this.ad = ad;
            this.bitmap = bitmap;
        }

        public Ad getAd() {
            return ad;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

    }

    private static final String TAG = "ShowAdTask";
    private Fragment fragment;
    private Context context;
    private NolotiroAPI api;
    private ProgressDialog progressDialog;

    public ShowAdTask(Fragment _fragment) {
        api = NolotiroAPI.getInstance();
        context = _fragment.getActivity();
        fragment = _fragment;
    }

    protected void onPreExecute() {
        String title = context.getResources().getString(R.string.please_wait);
        String message = context.getResources().getString(R.string.fetching_ad);
        progressDialog = ProgressDialog.show(fragment.getActivity(), title, message, true);
    }

    protected  void onPostExecute(final AdWithBitmap ad) {
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                TextView title = (TextView)fragment.getActivity().findViewById(R.id.textTitle);
                TextView description = (TextView)fragment.getActivity().findViewById(R.id.textDescription);
                ImageView image = (ImageView)fragment.getActivity().findViewById(R.id.imageImage);

                title.setText(ad.getAd().getTitle());
                description.setText(ad.getAd().getBody());
                image.setImageBitmap(ad.getBitmap());
            }
        });
        progressDialog.dismiss();
    }

    protected AdWithBitmap doInBackground(Integer... itemIds) {
        Integer itemId = itemIds[0];
        Ad ad = api.getAd(itemId);
        Bitmap bitmap = null;

        try {
            File f = new File(Utils.getNolotiroCacheDir(context) + ad.getImageFilename());
            Log.i("AdTask", f.toString());

            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(f.toString());
            } else {
                URL url = new URL(api.getPhotoUrlFromAd(ad));
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
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

        return new AdWithBitmap(ad, bitmap);
    }
}