/*
 * Copyright (c) 2013 "Pablo Castellano <pablo@anche.no>"
 * Copyright (c) 2013 "Eugenio Cano-Manuel Mendoza <eugeniocanom@gmail.com>"
 * Nolotiro App [http://nolotiro.org]
 *
 * This file is part of nolotiro-android.
 *
 * nolotiro-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

        //Log.i("getView: ", ad.getTitle());
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
