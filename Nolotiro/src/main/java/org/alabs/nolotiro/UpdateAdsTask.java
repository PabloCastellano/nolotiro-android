package org.alabs.nolotiro;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;

import java.util.List;

// This task retrieves ads and updates the ListView
// TODO: Params should be some kind of Action with opcode and real param: ex. "RetrieveGiveAds, woeid"
public class UpdateAdsTask extends AsyncTask<Integer, Void, List<Ad>> {

    private static final String TAG = "UpdateAdsTask";

    private ProgressDialog progress;
    private ListFragment fragment;
    private Context context;
    private NolotiroAPI nolotiro;

    public UpdateAdsTask(NolotiroAPI api, ListFragment _fragment) {
        nolotiro = api;
        context = _fragment.getActivity();
        fragment = _fragment;
        progress = new ProgressDialog(context);
        progress.setMessage("Loading...");
    }

    protected void onPreExecute() {
        progress.show();
    }

    protected  void onPostExecute(final List<Ad> ads) {

        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                AdListAdapter adapter;
                if(fragment.getListAdapter() == null) {
                    adapter = new AdListAdapter(fragment.getActivity(), ads);
                    fragment.setListAdapter(adapter);
                } else {
                    adapter = (AdListAdapter) fragment.getListAdapter();
                    for(Ad ad : ads) {
                        adapter.add(ad);
                    }
                }
                adapter.notifyDataSetChanged();

            }
        });
        progress.dismiss();
    }

    protected List<Ad> doInBackground(Integer... pages) {
        //TODO: get woeid from preferences
        List<Ad> ads = nolotiro.getGives(pages[0], 766273);

        return ads;
    }
}
