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
import java.util.ArrayList;
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

    // TODO: check isCancelled()
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
                    boolean isFav;
                    dba.openToWrite();

                    if(adapter == null) {
                        for(Ad ad : ads) {
                            dba.insertAd(ad);
                            isFav = dba.isAdFavorite(ad.getId());
                            ad.setFavorite(isFav);
                        }

                        adapter = new AdListAdapter(fragment.getActivity(), ads);
                        fragment.setListAdapter(adapter);


                    } else {
                        // addAll() is only available on API >= 11
                        for(Ad ad : ads) {
                            dba.insertAd(ad);
                            isFav = dba.isAdFavorite(ad.getId());
                            ad.setFavorite(isFav);
                            adapter.add(ad);
                        }

                    }
                    dba.close();
                    adapter.notifyDataSetChanged();
                } else {
                    fragment.setListAdapter(new AdListAdapter(fragment.getActivity(), new ArrayList<Ad>()));
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
