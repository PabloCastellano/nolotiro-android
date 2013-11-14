package org.alabs.nolotiro;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

// TODO: Just one instance of UpdateAdsTask
public class AdsFragment extends ListFragment {

    private static UpdateAdsTask updateTask;
    private static final Integer DEFAULT_WOEID = 766356;

    public AdsFragment() {
        updateTask = new UpdateAdsTask(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTask.execute(DEFAULT_WOEID);
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

}