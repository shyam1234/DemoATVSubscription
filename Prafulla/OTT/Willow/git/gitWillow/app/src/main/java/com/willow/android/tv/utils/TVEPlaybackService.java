package com.willow.android.tv.utils;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.willow.android.WillowApplication;
import com.willow.android.mobile.services.WiVolleySingleton;
import com.willow.android.tv.common.cards.CardRowsContainerFragment;
import com.willow.android.tv.ui.playback.PlaybackActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by abhishek gangwar on 10-05-2017.
 */

public class TVEPlaybackService {
    private static final String AUTHORIZE_REQUEST_URI = "/api/v1/authorize";
    private static final String MEDIA_TOKEN_REQUEST_URI = "/api/v1/tokens/media";

    private static final String AUTHORIZE_URI = "https://api.auth.adobe.com/api/v1/authorize";
    private static final String MEDIA_TOKEN_URI = "https://api.auth.adobe.com/api/v1/tokens/media";

    private static final String DEVICE_TYPE = "androidtv";
    private static final String APP_ID = "AndroidTV";
    private static String DEVICE_ID = "";
    private static Map requestParams = null;

    private static CardRowsContainerFragment rowsFragment;
    private static PlaybackActivity mainActivity;
    private static String mid;
    private static String encryptedContentId;

    private static PrefRepository prefRepository;

    private TVEPlaybackService() {
    }

    public static void setFragment(CardRowsContainerFragment fragment, String contentId){
        rowsFragment = fragment;
        encryptedContentId = WillowRestClient.getTVEPlaybackEncryptedContentId(contentId);
    }

    public static void setActivity(PlaybackActivity activity, String matchId){
        mainActivity = activity;
        prefRepository =  new PrefRepository(activity);
        mid = matchId;
    }

    public static void getAuthorizeHeader(Map params) {
        DEVICE_ID = prefRepository.getTVEDeviceID();
        requestParams = params;

        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(AUTHORIZE_REQUEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
            Request.Method.POST,
            WillowRestClient.TVE_AUTH_HEADER_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try
                    {
                        Timber.d("TVEPlaybackService :: %s", response);
                        JSONObject responseObject = new JSONObject(response);
                        String authHeader = responseObject.getString("auth_header");
                        getAuthorize(authHeader);
                    }
                    catch (JSONException e)
                    {
                        Log.e("DataDecodeError:", AUTHORIZE_REQUEST_URI);
                        mainActivity.showErrorPage("DataDecodeError:"+ AUTHORIZE_REQUEST_URI);

                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DataFetchError:", AUTHORIZE_REQUEST_URI);
                    mainActivity.showErrorPage("DataFetchError:"+ AUTHORIZE_REQUEST_URI);
                }
            }) {
                @Override
                protected Map getParams() { return authorizeRequestParams; }
            };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getMediaTokenHeader() {
        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(MEDIA_TOKEN_REQUEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
            Request.Method.POST,
            WillowRestClient.TVE_AUTH_HEADER_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try
                    {
                        Timber.d("TVEPlaybackService :: %s", response);

                        JSONObject responseObject = new JSONObject(response);
                        String authHeader = responseObject.getString("auth_header");
                        getMediaToken(authHeader);
                    }
                    catch (JSONException e)
                    {
                        Log.e("DataDecodeError:", MEDIA_TOKEN_REQUEST_URI);
                        mainActivity.showErrorPage("DataDecodeError:"+ MEDIA_TOKEN_REQUEST_URI);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DataFetchError:", MEDIA_TOKEN_REQUEST_URI);
                    mainActivity.showErrorPage("DataFetchError:"+ MEDIA_TOKEN_REQUEST_URI);
                }
            }) {
                @Override
                protected Map getParams() { return authorizeRequestParams; }
            };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }


    public static void getAuthorize(String authHeader) {
        String resource = "willow";
        if (prefRepository.isTVEProviderSpectrum()) {
            resource = "<rss version=\"2.0\"><channel><title>WILLOW</title><item><title>" + encryptedContentId + "</title></item></channel></rss>";
        }

        String url = AUTHORIZE_URI +  "?requestor=willow&deviceType=" + DEVICE_TYPE + "&deviceId=" + DEVICE_ID  + "&appId=" + APP_ID+ "&resource=" + resource;

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Timber.d("TVEPlaybackService :: %s", response);

                        String mvpd = response.getString("mvpd");
                        getMediaTokenHeader();
                    } catch (JSONException e) {
                        Log.e("DataDecodeError:", url);
                        mainActivity.showErrorPage("DataDecodeError:"+ url);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DataFetchError:", url);
                    mainActivity.showErrorPage("DataFetchError:"+ url);

                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError { return headers; }
            };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(jsonObjectRequest);
    }

    public static void getMediaToken(String authHeader) {
        String resource = "willow";
        if (prefRepository.isTVEProviderSpectrum()) {
            resource = "<rss version=\"2.0\"><channel><title>WILLOW</title><item><title>" + encryptedContentId + "</title></item></channel></rss>";
        }

        String url = MEDIA_TOKEN_URI +  "?requestor=willow&deviceType=" + DEVICE_TYPE + "&deviceId=" + DEVICE_ID  + "&appId=" + APP_ID + "&resource=" + resource;

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String serializedToken = null;
                    try {
                        Timber.d("TVEPlaybackService :: %s", response);

                        serializedToken = response.getString("serializedToken");
                        getStreamUrlWithToken(serializedToken);
                    } catch (JSONException e) {
                        Log.e("DataDecodeError:", url);
                        mainActivity.showErrorPage("DataDecodeError:"+ url);

                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DataFetchError:", url);
                    mainActivity.showErrorPage("DataFetchError:"+ url);

                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError { return headers; }
            };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(jsonObjectRequest);
    }

    public static void getStreamUrlWithToken(String mediaToken) {
        requestParams.put("token", mediaToken);

        mainActivity.getStreamingUrlForTVE(requestParams,mid);

//        if (mainActivity != null) {
//            mainActivity.getLiveHlsAndPlay(requestParams, mid);
//        } else if (rowsFragment != null){
//            rowsFragment.getHlsUrlForPlayback(requestParams);
//        }
    }
}
