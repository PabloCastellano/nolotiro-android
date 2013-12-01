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
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.alabs.nolotiro.db.DbAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


// This task queries Yahoo API in order to get the woeid of the queried area
// Obtained woeids will be saved into the DB for further requests
public class WoeidPlacesTask extends AsyncTask<String, Void, List<Woeid>> {

    private static final String TAG = "WoeidPlacesTask";

    private ProgressDialog progress;
    private Fragment fragment;
    private Context context;
    private String place = null;

    public WoeidPlacesTask(Fragment _fragment) {
        context = _fragment.getActivity();
        fragment = _fragment;
        //progress = new ProgressDialog(context);
        //progress.setMessage("Loading...");
    }

    protected void onPreExecute() {
        //progress.show();
    }

    protected  void onPostExecute(final List<Woeid> woeids) {

    }

    protected List<Woeid> doInBackground(String... places) {
        place = places[0];
        List<Woeid> woeids = new ArrayList<Woeid>();
        //DbAdapter dba = new DbAdapter(context);
        //dba.openToWrite();

        try {
            JSONObject jObject = queryYahooWoeid(place);
            Object o = jObject.getJSONObject("query").getJSONObject("results").get("place");
            Woeid woeid;

            if (o instanceof JSONArray) {
                JSONArray ja = (JSONArray) o;
                Log.i(TAG, "Number of places with similar name: " + ja.length());

                for (int i=0; i < ja.length(); i++)
                {
                    woeid = jsonArrayToWoeid(ja, i);
                    woeids.add(woeid);
                    //dba.insertWoeid(woeid);
                    Log.i(TAG, "Place: " + woeid);
                }
            } else if (o instanceof JSONObject) {
                JSONObject ja = (JSONObject) o;

                woeid = jsonObjectToWoeid(ja);
                woeids.add(woeid);
                //dba.insertWoeid(woeid);
                Log.i(TAG, "Place: " + woeid);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            //dba.close();
        }

        return woeids;
    }

    // Query Yahoo api in order to get possible woeids and return JSONObject with response
    // TODO: where ... and lang="es"
    private JSONObject queryYahooWoeid(String place) throws JSONException {
        place = Utils.removeSpecialChars(place);
        String request = "http://query.yahooapis.com/v1/public/yql?q=select%20name,%20country,%20admin1,%20woeid%20from%20geo.places%20where%20text%3D%22" + place + "%22%20and%20lang%3D%22es%22&format=json";
        HttpURLConnection urlConnection = null;
        JSONObject jo = null;

        Log.i(TAG, request);

        try {
            URL url = new URL(request);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            jo = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return jo;
    }

    // Parse JSONObject and return Woeid object
    private Woeid jsonObjectToWoeid(JSONObject ja) throws JSONException {
        String name, region, country, id;

        name = ja.getString("name");
        id = ja.getString("woeid");
        region = ja.getJSONObject("admin1").getString("content");
        country = ja.getJSONObject("country").getString("content");
        return new Woeid(Integer.parseInt(id), name, region, country);
    }

    // Parse JSONArray and return Woeid object
    private Woeid jsonArrayToWoeid(JSONArray ja, int i) throws JSONException {
        String name, region, country, id;
        JSONObject jo = ja.getJSONObject(i);

        name = jo.getString("name");
        id = jo.getString("woeid");
        region = jo.getJSONObject("admin1").getString("content");
        country = jo.getJSONObject("country").getString("content");
        return new Woeid(Integer.parseInt(id), name, region, country);
    }
}
