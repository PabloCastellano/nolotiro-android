package org.alabs.nolotiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

    private Fragment fragment;
    private Context context;
    private NolotiroAPI nolotiro;

    public ShowAdTask(Fragment _fragment) {
        nolotiro = NolotiroAPI.getInstance();
        context = _fragment.getActivity();
        fragment = _fragment;
    }

    protected void onPreExecute() {

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
    }

    protected AdWithBitmap doInBackground(Integer... itemIds) {
        Integer itemId = itemIds[0];
        Ad ad = nolotiro.getAd(itemId);
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(ad.getPhoto()).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AdWithBitmap(ad, bitmap);
    }
}
