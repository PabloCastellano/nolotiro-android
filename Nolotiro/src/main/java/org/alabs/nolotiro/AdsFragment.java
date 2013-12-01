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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class AdsFragment extends ListFragment {

    private static final String TAG = "AdsFragment";
    private static final Integer DEFAULT_WOEID = 766356;

    boolean recreated;

    public AdsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        recreated = false;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int id = sharedPref.getInt("current_woeid", Utils.DEBUG_WOEID);
        setScrollListener(id);
        if(!recreated) {
            refreshAds(id);
            recreated = true;
            // TODO: Restore position
        }
    }

    public void setScrollListener(int id) {
        this.getListView().setOnScrollListener(new EndlessScrollListener(id));
    }

    public void unsetScrollListener() {
        this.getListView().setOnScrollListener(null);
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Save position
    }

    public void onListItemClick(ListView listView, View view, int position, long id) {
        Ad ad = (Ad) listView.getAdapter().getItem(position);
        Log.i(TAG, "Clicked on " + ad.getTitle());

        startActivity(new Intent(getActivity(), AdViewActivity.class).putExtra("ad", ad));
    }

    public void refreshAds(int id) {
        Log.i(TAG, "refreshAds()");
        unsetScrollListener();
        UpdateAdsTask updateTask = new UpdateAdsTask(this, id);
        updateTask.execute(1);
        setScrollListener(id);
    }

    //Seen on http://benjii.me/2010/08/endless-scrolling-listview-in-android/
    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 1;
        private int previousTotal = 0;
        private boolean loading = true;
        private int id;

        public EndlessScrollListener(int _id) {
            id = _id;
        }

        //public EndlessScrollListener(int visibleThreshold) {
        //    this.visibleThreshold = visibleThreshold;
        //}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                //Log.i(TAG, "Loading next page: " + nolotiroPage + " (currentPage=" + currentPage);
                Log.i(TAG, "Loading next page: " + (currentPage));
                new UpdateAdsTask(AdsFragment.this, id).execute(currentPage);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}
