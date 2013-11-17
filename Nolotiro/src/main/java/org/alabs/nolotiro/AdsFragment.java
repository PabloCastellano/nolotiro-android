package org.alabs.nolotiro;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class AdsFragment extends ListFragment {

    private static final Integer DEFAULT_WOEID = 766356;
    private static final String TAG = "AdsFragment";

    public AdsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshAds();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Restore position
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Save position
    }

    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // TODO: Show ad in detail
    }

    public void refreshAds() {
        UpdateAdsTask updateTask = new UpdateAdsTask(this);
        updateTask.execute(DEFAULT_WOEID);
        Log.i(TAG, "Refresh");
    }
}