package com.willow.android.tv.utils.analytics;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.willow.android.WillowApplication;
import com.willow.android.mobile.services.WiVolleySingleton;
import com.willow.android.tv.utils.PrefRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by abhishek on 14/07/17.
 */

public class WiAnalytics {
    public static final String WILLOW_LOG_URL = "https://eventlog.willow.tv/mapplogs";
    private static String deviceType = "ANDROIDTV";
    private static String replayCategory = "REPLAY";
    private static String highlightsCategory = "HIGHLIGHTS";
    private static String liveCategory = "LIVE";
    private static String eventBaseName = "PLAY";

    public static void sendReplayPlayEvent(String matchId, String seriesId, String videoTitle){
        PrefRepository prefRepository = new PrefRepository(WillowApplication.Companion.getAnalyticsContext());
        String eventName = eventBaseName + "_REPLAY_" + deviceType;
        String jsonString = "{\"DeviceType\": \"" + deviceType + "\",  \"Category\":\"" + replayCategory + "\", \"EventName\": \"" + eventName + "\",  \"VideoTitle\":\"" + videoTitle + "\", \"MatchId\":\"" + matchId + "\", \"SeriesId\":\"" + seriesId + "\", \"UserId\":\"" + prefRepository.getUserID() + "\", \"SubscriptionStatus\" :\"" + getUserSubscriptionStatusString() + "\"}";

        postWillowVideoLog(jsonString);
    }

    public static void sendLivePlayEvent(String matchId, String seriesId, String videoTitle){
        PrefRepository prefRepository = new PrefRepository(WillowApplication.Companion.getAnalyticsContext());
        String eventName = eventBaseName + "_LIVE_" + deviceType;
        String jsonString = "{\"DeviceType\": \"" + deviceType + "\",  \"Category\":\"" + liveCategory + "\", \"EventName\": \"" + eventName + "\",  \"VideoTitle\":\"" + videoTitle + "\", \"MatchId\":\"" + matchId + "\", \"SeriesId\":\"" + seriesId + "\", \"UserId\":\"" + prefRepository.getUserID() + "\", \"SubscriptionStatus\" :\"" + getUserSubscriptionStatusString() + "\"}";

        postWillowVideoLog(jsonString);
    }

    public static void sendHighlightPlayEvent(String matchId, String seriesId, String videoTitle, String ytRecordId){
        PrefRepository prefRepository = new PrefRepository(WillowApplication.Companion.getAnalyticsContext());
        String eventName = eventBaseName + "_HIGHLIGHTS_" + deviceType;
        String jsonString = "{\"DeviceType\": \"" + deviceType + "\",  \"Category\":\"" + highlightsCategory + "\", \"EventName\": \"" + eventName + "\",  \"VideoTitle\":\"" + videoTitle + "\", \"MatchId\":\"" + matchId + "\", \"SeriesId\":\"" + seriesId + "\",  \"YTRecordId\":\"" + ytRecordId + "\", \"UserId\":\"" + prefRepository.getUserID()+ "\", \"SubscriptionStatus\" :\"" + getUserSubscriptionStatusString() + "\"}";

        postWillowVideoLog(jsonString);
    }

    private static String getUserSubscriptionStatusString(){
        PrefRepository prefRepository = new PrefRepository(WillowApplication.Companion.getAnalyticsContext());
        return Boolean.TRUE.equals(prefRepository.getUserSubscribed()) ? "1" : "0";
    }

    public static void postWillowVideoLog(String json_data) {
        Map requestParams   = new HashMap();
        requestParams.put("json_data",json_data);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                WILLOW_LOG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DataFetchError:", WILLOW_LOG_URL);
                    }
                })
        {
            @Override
            protected Map getParams() {
                return requestParams;
            }
        };

        WiVolleySingleton.Companion.getInstance(Objects.requireNonNull(WillowApplication.Companion.getAppContext())).addToRequestQueue(stringRequest);
    }
}
