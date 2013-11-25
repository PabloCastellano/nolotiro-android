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

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

// This task retrieves ads and updates the ListView
// TODO: Params should be some kind of Action with opcode and real param: ex. "RetrieveGiveAds, woeid"
public class UpdateAdsTask extends AsyncTask<Integer, Void, List<Ad>> {

    private static final String TAG = "UpdateAdsTask";

    private ProgressDialog progress;
    private ListFragment fragment;
    private Context context;
    private NolotiroAPI nolotiro;
    private Integer page = 1;
    private String errorMessage = null;

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
                    if(adapter == null) {
                        adapter = new AdListAdapter(fragment.getActivity(), ads);
                        fragment.setListAdapter(adapter);
                    } else {
                        // addAll() is only available on API >= 11
                        for(Ad ad : ads) {
                            adapter.add(ad);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    fragment.setListAdapter(null);
                    Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        progress.dismiss();
    }

    //TODO: get woeid from preferences
    protected List<Ad> doInBackground(Integer... pages) {
        page = pages[0];
        List<Ad> ads = null;

        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            errorMessage = context.getResources().getString(R.string.error_connecting);
            return null;
        }

        try {
            ads = nolotiro.getGives(page, 766273);
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
