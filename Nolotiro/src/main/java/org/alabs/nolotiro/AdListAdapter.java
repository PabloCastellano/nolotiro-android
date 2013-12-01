package org.alabs.nolotiro;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
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
            TextView dateView = (TextView) rowView.findViewById(R.id.text_date);

            titleView.setText(ad.getTitle());
            descriptionView.setText(ad.getBody());
            Date date = Utils.ISO8601ToDate(ad.getDate());
            String adDate = getAdDate(date);

            dateView.setText(adDate);
            ImageView image = (ImageView) rowView.findViewById(R.id.imageView);
            LoadThumbnailTask task = new LoadThumbnailTask(activity, image);
            task.execute(ad);
        }

        Log.i("getView: ", ad.getTitle());
        return rowView;
    }

    private String getAdDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(date.getTime());

        String response = "";

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));

        float now = System.currentTimeMillis()/1000;
        float adDate = date.getTime()/1000;

        float daysSince = (now - adDate)/86400;

        if(daysSince > 1) {
            response = String.valueOf((int)Math.floor(daysSince)) + "d";
        } else {
            response = hour + ":" + minute;
        }

        return response;

    }
}
