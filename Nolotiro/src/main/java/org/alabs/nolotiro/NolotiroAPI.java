package org.alabs.nolotiro;

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

    private static final String TAG = "NolotiroAPI";
    private static final String BASE_API_ENDPOINT = "http://beta.nolotiro.org/%s";
    private static final String BASE_API_ENDPOINT_OLD = "http://nolotiro.org";
    private static final String AD_API_ENDPOINT = "/ad/%d/api.json";
    private static final String AD_PHOTO_API_ENDPOINT = "/images/uploads/ads/original/%s";

    private Map<Integer, Ad> cache;

    private int woeId;
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

    public List<Ad> getGives(int offset) {
        //TODO: Implement when rest api is complete

        List<Ad> ads = new ArrayList<Ad>();
        ads.add(getAd(153841));
        ads.add(getAd(153840));
        ads.add(getAd(153839));

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
            ad.setTitle(json.getString("title"));
            ad.setBody(json.getString("body"));
            ad.setUsername(json.getString("user_owner"));
            ad.setStatus(Ad.Status.valueOf(json.getString("status").toUpperCase()));
            ad.setImageFilename(json.getString("image_file_name"));
            ad.setWoeid(json.getInt("woeid_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ad;
    }

    public String getPhotoUrlFromAd(Ad ad) {
        return String.format(BASE_API_ENDPOINT_OLD + AD_PHOTO_API_ENDPOINT, ad.getImageFilename());
    }
}
