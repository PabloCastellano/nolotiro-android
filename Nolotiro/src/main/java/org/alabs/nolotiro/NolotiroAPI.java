package org.alabs.nolotiro;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NolotiroAPI {

    private static final NolotiroAPI INSTANCE = new NolotiroAPI();

    private static final String TAG = "NolotiroAPI";
    private static final String DEFAULT_BASE_ENDPOINT = "http://beta.nolotiro.org";
    private static final String OLD_BASE_ENDPOINT = "http://nolotiro.org";
    private static final String AD_API_ENDPOINT = "/api/v1/ad/%d";
    private static final String AD_PHOTO_URL = "/images/uploads/ads/original/%s";
    private static final String AD_THUMB_URL = "/images/uploads/ads/100/%s";
    private static final String LIST_GIVES_BY_WOEID = "/api/v1/woeid/%d/give?page=%d";
    private static final String LIST_WANTS_BY_WOEID = "/api/v1/woeid/%d/want?page=%d";
    private static final String LIST_WOEIDS = "/api/v1/woeid/list";

    private Map<Integer, Ad> cache;

    private String langId;
    private String hostname;

    private NolotiroAPI() {
        cache = new HashMap<Integer, Ad>();
        hostname = DEFAULT_BASE_ENDPOINT;
        langId = "es";
        INIT_API_CREDENTIALS();
    }

    public static NolotiroAPI getInstance() {
        return INSTANCE;
    }

    private void INIT_API_CREDENTIALS() {
        //TODO: DO NOT COMMIT
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("nolotiro", "sincondiciones".toCharArray());
            }
        });
    }

    public Ad getAd(int id) throws JSONException, IOException {
        if(cache.containsKey(id)) {
            return cache.get(id);
        }

        Ad ad = null;

        String requestURL = String.format(hostname + AD_API_ENDPOINT, id);
        JSONObject adJSON = makeRequest(requestURL);
        ad = jsonToAd(adJSON);
        ad.setId(id);

        cache.put(id, ad);

        return ad;
    }

    public List<Ad> getWants(int offset, int woeId) throws IOException, JSONException {
        String requestURL = String.format(hostname + LIST_WANTS_BY_WOEID, woeId, offset);
        List<Ad> ads = new ArrayList<Ad>();

        JSONObject response = makeRequest(requestURL);
        JSONArray adsJSON = new JSONArray(response.getString("ads"));
        for(int i = 0; i < adsJSON.length(); i++) {
            ads.add(jsonToAd(adsJSON.getJSONObject(i)));
        }

        return ads;
    }

    public List<Ad> getGives(int offset, int woeId) throws IOException, JSONException {
        String requestURL = String.format(hostname + LIST_GIVES_BY_WOEID, woeId, offset);
        List<Ad> ads = new ArrayList<Ad>();

        JSONObject response = makeRequest(requestURL);
        JSONArray adsJSON = new JSONArray(response.getString("ads"));
        for(int i = 0; i < adsJSON.length(); i++) {
            ads.add(jsonToAd(adsJSON.getJSONObject(i)));
        }

        return ads;
    }

    private JSONObject makeRequest(String url) throws IOException, JSONException {
        URL requestURL = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
        urlConnection.setDoOutput(false);
        urlConnection.setRequestMethod("GET");
        String response = "";

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = readInputStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return new JSONObject(response);
    }

    private String readInputStream(InputStream in) {
        String result = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                result += inputLine;
            }
        } catch (IOException e) {
            return null;
        }

        return result;
    }

    private Ad jsonToAd(JSONObject json) {
        Ad ad = new Ad();

        try {
            if (json.has("id")) { ad.setId(Integer.valueOf(json.getString("id"))); }
            if (json.has("title")) { ad.setTitle(json.getString("title")); }
            if (json.has("body")) { ad.setBody(json.getString("body")); }
            if (json.has("user_owner")) { ad.setUsername(json.getString("user_owner")); }
            if (json.has("type")) {
                int t = json.getInt("woeid_code");
                if (t == 1)
                    ad.setType(Ad.Type.GIVE);
                else
                    ad.setType(Ad.Type.WANT);
            }
            if (json.has("woeid_code")) { ad.setWoeid(json.getInt("woeid_code")); }
            if (json.has("date_created")) { ad.setDate(json.getString("date_created")); }
            if (json.has("image_file_name")) { ad.setImageFilename(json.getString("image_file_name")); }
            try {
                if (json.has("status")) { ad.setStatus(Ad.Status.valueOf(json.getString("status").toUpperCase())); }
            } catch (IllegalArgumentException e) {
                //TODO: Use UNKNOWN or similar
                ad.setStatus(Ad.Status.DELIVERED);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return ad;
    }

    // Returns null if there's no photo
    public URL getPhotoUrlFromAd(Ad ad) throws MalformedURLException {
        String filename = ad.getImageFilename();
        if(filename == null || filename.equals("null"))
            return null;
        URL url = new URL(String.format(OLD_BASE_ENDPOINT + AD_PHOTO_URL, ad.getImageFilename()));
        return url;
    }

    // Returns null if there's no photo
    public URL getThumbnailUrlFromAd(Ad ad) throws MalformedURLException {
        String filename = ad.getImageFilename();
        if(filename == null || filename.equals("null"))
            return null;
        URL url = new URL(String.format(OLD_BASE_ENDPOINT + AD_THUMB_URL, ad.getImageFilename()));
        return url;
    }

    public void setHostname(String _hostname) {
        hostname = _hostname;
    }

    public void setLangId(String _langId) {
        langId = _langId;
    }
}
