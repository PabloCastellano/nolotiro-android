package org.alabs.nolotiro;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.alabs.nolotiro.db.DbAdapter;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

// This task retrieves ads and updates the ListView
// TODO: Params should be some kind of Action with opcode and real param: ex. "RetrieveGiveAds, woeid"
public class UpdateAdsTask extends AsyncTask<Integer, Void, List<Ad>> {

    private static final String TAG = "UpdateAdsTask";

    //private ProgressDialog progress;
    private ListFragment fragment;
    private Context context;
    private NolotiroAPI api;
    private Integer page = 1;
    private Integer woeid = Utils.DEBUG_WOEID;
    private String errorMessage = null;

    public UpdateAdsTask(ListFragment _fragment, Integer _woeid) {
        api = NolotiroAPI.getInstance();
        context = _fragment.getActivity();
        fragment = _fragment;
        woeid = _woeid;
       // progress = new ProgressDialog(context);
       // progress.setMessage(context.getResources().getString(R.string.loading));
    }

    protected void onPreExecute() {
        //progress.show();
    }

    protected void onPostExecute(final List<Ad> ads) {
        final String finalMessage = errorMessage;

        if (errorMessage != null) {
            Log.e(TAG, errorMessage);
        }

        // Set AdListAdapter and show Toast message if there was an exception
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (ads != null) {
                    AdListAdapter adapter = (AdListAdapter) fragment.getListAdapter();
                    DbAdapter dba = new DbAdapter(context);
                    dba.openToWrite();

                    if(adapter == null) {
                        adapter = new AdListAdapter(fragment.getActivity(), ads);
                        fragment.setListAdapter(adapter);

                        for(Ad ad : ads) {
                            dba.insertAd(ad);
                        }

                    } else {
                        // addAll() is only available on API >= 11
                        for(Ad ad : ads) {
                            adapter.add(ad);
                            dba.insertAd(ad);
                        }

                    }
                    dba.close();
                    adapter.notifyDataSetChanged();
                } else {
                    fragment.setListAdapter(null);
                    Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        //progress.dismiss();
    }

    protected List<Ad> doInBackground(Integer... pages) {
        page = pages[0];
        List<Ad> ads = null;
        boolean isInternet = Utils.isInternetAvailable(context);

        // TODO: If there's no Internet, then try to load last X ads from cache
        if (!isInternet) {
            errorMessage = context.getResources().getString(R.string.error_connecting);
            return null;
        }

        try {
            ads = api.getGives(page, woeid);
        } catch (IOException e) {
            errorMessage = context.getResources().getString(R.string.error_retrieving_ads);
            ads = null;
        } catch (JSONException e) {
            errorMessage = context.getResources().getString(R.string.error_retrieving_ads);
            ads = null;
        }

        return ads;
    }
}
