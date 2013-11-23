package org.alabs.nolotiro;

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
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NolotiroAPI {

    private static final NolotiroAPI INSTANCE = new NolotiroAPI();

    private static final String BASE_API_ENDPOINT = "http://beta.nolotiro.org";
    private static final String BASE_API_ENDPOINT_OLD = "http://nolotiro.org";
    private static final String AD_API_ENDPOINT = "/ad/%d/api.json";
    private static final String AD_PHOTO_API_ENDPOINT = "/images/uploads/ads/original/%s";
    private static final String GIVE_LIST_BY_WOEID_ENDPOINT = "/api/v1/woeid/%d/give";

    private Map<Integer, Ad> cache;

    private String langId = "es";

    private NolotiroAPI() {
        cache = new HashMap<Integer, Ad>();
        INIT_API_CREDENTIALS();
    }

    public static NolotiroAPI getInstance() {
        return INSTANCE;
    }

    private void INIT_API_CREDENTIALS() {
    }

    public Ad getAd(int id) {
        if(cache.containsKey(id)) {
            return cache.get(id);
        }

        Ad ad = null;

        String requestURL = String.format(BASE_API_ENDPOINT + AD_API_ENDPOINT, langId, id);
        try {
            JSONObject adJSON = makeRequest(requestURL);
            ad = jsonToAd(adJSON);
            ad.setId(id);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cache.put(id, ad);

        return ad;
    }

    public List<Ad> getWants(int offset) {
        //TODO: Implement when rest api is complete
        return null;
    }

    public List<Ad> getGives(int offset, int woeId) {

        String requestURL = String.format(BASE_API_ENDPOINT + GIVE_LIST_BY_WOEID_ENDPOINT, woeId);
        List<Ad> ads = new ArrayList<Ad>();

        try {
            JSONObject response = makeRequest(requestURL);
            JSONArray adsJSON = new JSONArray(response.getString("ads"));
            for(int i = 0; i < adsJSON.length(); i++) {
                ads.add(jsonToAd(adsJSON.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            if (json.has("status")) { ad.setStatus(Ad.Status.valueOf(json.getString("status"))); }
            if (json.has("image_file_name")) { ad.setImageFilename(json.getString("image_file_name")); }
            if (json.has("woeid_code")) { ad.setWoeid(json.getInt("woeid_code")); }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return ad;
    }

    public String getPhotoUrlFromAd(Ad ad) {
        return String.format(BASE_API_ENDPOINT_OLD + AD_PHOTO_API_ENDPOINT, ad.getImageFilename());
    }
}
