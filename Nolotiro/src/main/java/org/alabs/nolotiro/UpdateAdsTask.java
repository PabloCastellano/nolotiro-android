package org.alabs.nolotiro;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;
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
        //progress = new ProgressDialog(context);
        //progress.setMessage("Loading...");
    }

    protected void onPreExecute() {
        //progress.show();
    }

    protected  void onPostExecute(final List<Ad> ads) {
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                /*
                if (ads == null) {
                    Log.w(TAG, "onPostExecute: ads is null")
                }

                for (Ad a : ads) {
                    // TODO: cache images
                }
                */

                AdListAdapter adapter = new AdListAdapter(fragment.getActivity(), ads);
                fragment.setListAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });
        //progress.dismiss();
    }

    protected List<Ad> doInBackground(Integer... woeids) {
        Integer woeid = woeids[0];
        List<Ad> ads = nolotiro.getGives(0);

        // Meanwhile...
/*        Ad ad = new Ad();
        ad.setId(1);
        ad.setTitle("Gameboy");
        ad.setBody("My super gameboy is nice!");
        ads.add(ad);

        ad = new Ad();
        ad.setId(2);
        ad.setTitle("Motor ACME");
        ad.setBody("Vendo este super motor!");
        ads.add(ad);*/

        //if (isCancelled()) break;

        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return ads;
    }
}
