package org.alabs.nolotiro;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WoeidPlacesTask extends AsyncTask<String, Void, List<Woeid>> {

    private static final String TAG = "WoeidPlacesTask";

    private ProgressDialog progress;
    private Fragment fragment;
    private Context context;
    private String place = null;

    public WoeidPlacesTask() {

    }

    public WoeidPlacesTask(Fragment _fragment) {
        context = _fragment.getActivity();
        fragment = _fragment;
        //progress = new ProgressDialog(context);
        //progress.setMessage("Loading...");
    }

    protected void onPreExecute() {
        //progress.show();
    }

    protected  void onPostExecute(final List<String> places) {
        /*
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // Save current woeid
            }
        });
        */
        //progress.dismiss();
    }

    // TODO: where ... and lang="es"
    // TODO: cuidado tildes: "Córdoba"
    protected List<Woeid> doInBackground(String... places) {
        place = places[0];
        List<Woeid> woeids = new ArrayList<Woeid>();
        String request = "http://query.yahooapis.com/v1/public/yql?q=select%20name,%20country,%20admin1,%20woeid%20from%20geo.places%20where%20text%3D%22" + place + "%22&format=json";
        Log.i(TAG, request);

        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(request);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            String name, region, country, woeid;
            String placename;

            JSONObject jObject = new JSONObject(sb.toString());
            Object o = jObject.getJSONObject("query").getJSONObject("results").get("place");
            if (o instanceof JSONArray) {
                JSONArray ja = (JSONArray) o;
                Log.i(TAG, "Number of places with same name: " + ja.length());

                for (int i=0; i < ja.length(); i++)
                {
                    name = ja.getJSONObject(i).getString("name");
                    woeid = ja.getJSONObject(i).getString("woeid");
                    region = ja.getJSONObject(i).getJSONObject("admin1").getString("content");
                    country = ja.getJSONObject(i).getJSONObject("country").getString("content");
                    placename = name + ", " + region + ", " + country + " (" + woeid + ")";
                    Log.i(TAG, "Place: " + placename);
                    woeids.add(new Woeid(Integer.parseInt(woeid), name, region, country));
                }
            } else if (o instanceof JSONObject) {
                JSONObject ja = (JSONObject) o;

                name = ja.getString("name");
                woeid = ja.getString("woeid");
                region = ja.getJSONObject("admin1").getString("content");
                country = ja.getJSONObject("country").getString("content");
                placename = name + ", " + region + ", " + country + " (" + woeid + ")";
                Log.i(TAG, "Place: " + placename);
                woeids.add(new Woeid(Integer.parseInt(woeid), name, region, country));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return woeids;
    }
}
