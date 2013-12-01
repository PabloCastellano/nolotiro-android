package org.alabs.nolotiro;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdListAdapter extends ArrayAdapter<Ad> {

    private Activity activity;

    public AdListAdapter(Activity _activity, List<Ad> ads) {
        super(_activity, 0, ads);
        activity = _activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.fragment_main, null);
        Ad ad = getItem(position);

        if(ad != null) {
            TextView titleView = (TextView) rowView.findViewById(R.id.ad_title_text);
            TextView descriptionView = (TextView) rowView.findViewById(R.id.itemDescription);
            titleView.setText(ad.getTitle());
            descriptionView.setText(ad.getBody());
            ImageView image = (ImageView) rowView.findViewById(R.id.imageView);
            LoadThumbnailTask task = new LoadThumbnailTask(activity, image);
            task.execute(ad);
        }

        Log.i("getView: ", ad.getTitle());
        return rowView;
    }
}
