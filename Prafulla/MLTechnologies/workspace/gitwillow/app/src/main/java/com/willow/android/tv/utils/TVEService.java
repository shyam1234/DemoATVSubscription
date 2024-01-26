package com.willow.android.tv.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.willow.android.WillowApplication;
import com.willow.android.mobile.services.WiVolleySingleton;
import com.willow.android.tv.ui.login.TVEActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abhishek gangwar on 10-05-2017.
 */

public class TVEService {
    private static final String REGCODE_REQUEST_URI = "/reggie/v1/willow/regcode";
    private static final String AUTHN_REQUEST_URI = "/api/v1/tokens/authn";
    private static final String AUTHORIZE_REQUEST_URI = "/api/v1/authorize";
    private static final String MEDIA_TOKEN_REQUEST_URI = "/api/v1/tokens/media";
    private static final String LOGOUT_REUQEST_URI = "/api/v1/logout";

    private static final String REGCODE_URI = "https://api.auth.adobe.com/reggie/v1/willow/regcode";
    private static final String AUTHN_URI = "https://api.auth.adobe.com/api/v1/tokens/authn";
    private static final String AUTHORIZE_URI = "https://api.auth.adobe.com/api/v1/authorize";
    private static final String MEDIA_TOKEN_URI = "https://api.auth.adobe.com/api/v1/tokens/media";
    private static final String LOGOUT_URI = "https://api.auth.adobe.com/api/v1/logout";

    private static final String TOKEN_VERIFY_URI = "https://ddev.willow.tv/verify_tv_provider_token";

    private static final String DEVICE_TYPE = "androidtv";
    private static final String APP_ID = "AndroidTV";
    private static String DEVICE_ID = "";
    static TVEActivity tveActivity;

    private TVEService() {

    }

    public static void setTVEActivity(TVEActivity activity) {
        tveActivity = activity;
    }

    public static void getRegCodeHeader() {
        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(REGCODE_REQUEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                WillowRestClient.TVE_AUTH_HEADER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String authHeader = responseObject.getString("auth_header");
                            DEVICE_ID = responseObject.getString("deviceId");
                            getRegCode(authHeader);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", REGCODE_REQUEST_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", REGCODE_REQUEST_URI);
                    }
                }) {
            @Override
            protected Map getParams() { return authorizeRequestParams; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getAuthNHeader() {
        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(AUTHN_REQUEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                WillowRestClient.TVE_AUTH_HEADER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String authHeader = responseObject.getString("auth_header");
                            getAuthN(authHeader);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", AUTHN_REQUEST_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", AUTHN_REQUEST_URI);
                    }
                }) {
            @Override
            protected Map getParams() { return authorizeRequestParams; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getAuthorizeHeader() {
        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(AUTHORIZE_REQUEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                WillowRestClient.TVE_AUTH_HEADER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String authHeader = responseObject.getString("auth_header");
                            getAuthorize(authHeader);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", AUTHORIZE_REQUEST_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", AUTHORIZE_REQUEST_URI);
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
                            JSONObject responseObject = new JSONObject(response);
                            String authHeader = responseObject.getString("auth_header");
                            getMediaToken(authHeader);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", MEDIA_TOKEN_REQUEST_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", MEDIA_TOKEN_REQUEST_URI);
                    }
                }) {
            @Override
            protected Map getParams() { return authorizeRequestParams; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getLogoutHeader() {
        Map authorizeRequestParams = WillowRestClient.getTVEAuthHeaderParams(LOGOUT_REUQEST_URI, "GET");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                WillowRestClient.TVE_AUTH_HEADER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String authHeader = responseObject.getString("auth_header");
                            tveLogout(authHeader);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", LOGOUT_REUQEST_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", LOGOUT_REUQEST_URI);
                    }
                }) {
            @Override
            protected Map getParams() { return authorizeRequestParams; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }



    public static void getRegCode(String authHeader) {
        Map params = new HashMap();
        params.put("deviceType", DEVICE_TYPE);
        params.put("deviceId", DEVICE_ID);
        params.put("appId", APP_ID);
        params.put("ttl", "3600");

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REGCODE_URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String regCode = responseObject.getString("code");
                            tveActivity.setRegCodeText(regCode);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", REGCODE_URI);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", REGCODE_URI);
                    }
                }) {
            @Override
            protected Map getParams() { return params; }

            @Override
            public Map getHeaders() throws AuthFailureError { return headers; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getAuthN(String authHeader) {
        String url = AUTHN_URI +  "?requestor=willow&deviceType=" + DEVICE_TYPE + "&deviceId=" + DEVICE_ID  + "&appId=" + APP_ID;

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        StringRequest stringRequest = new StringRequest(
            Request.Method.GET,
            url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try
                    {
                        JSONObject responseObject = new JSONObject(response);
                        String mvpd = responseObject.getString("mvpd");
                        getAuthorizeHeader();
                    }
                    catch (JSONException e)
                    {
                        Log.e("DataDecodeError:", url);
                        tveActivity.showAuthError();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DataFetchError:", url);
                    tveActivity.showAuthError();
                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError { return headers; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getAuthorize(String authHeader) {
        String url = AUTHORIZE_URI +  "?requestor=willow&deviceType=" + DEVICE_TYPE + "&deviceId=" + DEVICE_ID  + "&appId=" + APP_ID+ "&resource=willow";

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String mvpd = responseObject.getString("mvpd");
                            getMediaTokenHeader();
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", url);
                            tveActivity.showAuthError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", url);
                        if(error.networkResponse.data!=null) {
                            try {
                                String body = new String(error.networkResponse.data,"UTF-8");
                                try
                                {
                                    JSONObject responseObject = new JSONObject(body);
                                    String details = responseObject.getString("details");
                                    tveActivity.showReceivedError(details);
                                }
                                catch (JSONException e) {
                                    tveActivity.showAuthError();
                                }
                            } catch (UnsupportedEncodingException e) {
                                tveActivity.showAuthError();
                            }
                        } else {
                            tveActivity.showAuthError();
                        }
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError { return headers; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void getMediaToken(String authHeader) {
        String url = MEDIA_TOKEN_URI +  "?requestor=willow&deviceType=" + DEVICE_TYPE + "&deviceId=" + DEVICE_ID  + "&appId=" + APP_ID + "&resource=willow";

        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            String serializedToken = responseObject.getString("serializedToken");
                            verifyMediaTokenFromWillow(serializedToken);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", url);
                            tveActivity.showAuthError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", url);
                        tveActivity.showAuthError();
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError { return headers; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }


    public static void verifyMediaTokenFromWillow(String mediaToken) {
        Map params = new HashMap();
        params.put("device_type", DEVICE_TYPE);
        params.put("token", mediaToken);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                TOKEN_VERIFY_URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject responseObject = new JSONObject(response);
                            Integer userIdInt = responseObject.getInt("userId");
                            String provider = responseObject.getString("Provider");
                            String adsCategory = responseObject.getString("ads_category");
                            int subscriptionStatus = responseObject.getInt("subscriptionStatus");
                            boolean enableDfpForVOD = responseObject.getBoolean("EnableDfpForVOD");
                            boolean enableDfpForLive = responseObject.getBoolean("EnableDfpForLive");
                            String userId = String.valueOf(userIdInt);
                            tveActivity.storeAuthenticatedUser(userId, DEVICE_ID, provider, adsCategory
                            ,subscriptionStatus , enableDfpForLive,enableDfpForVOD);
                        }
                        catch (JSONException e)
                        {
                            Log.e("DataDecodeError:", TOKEN_VERIFY_URI);
                            tveActivity.showAuthError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", TOKEN_VERIFY_URI);
                        tveActivity.showAuthError();
                    }
                }) {
            @Override
            protected Map getParams() { return params; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }

    public static void tveLogout(String authHeader){
        LinkedHashMap headers = new LinkedHashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", authHeader);

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                LOGOUT_URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", LOGOUT_URI);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError { return headers; }
        };
        WiVolleySingleton.Companion.getInstance(WillowApplication.instance).addToRequestQueue(stringRequest);
    }
}

