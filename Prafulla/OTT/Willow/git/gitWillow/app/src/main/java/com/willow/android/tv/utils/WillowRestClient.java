package com.willow.android.tv.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;



public class WillowRestClient
{
    public static final String TAG = WillowRestClient.class.getSimpleName();

//    public static final String md5Key                  = "iqwue4522404c264f36ca4031500143eiixmm38shksgvbx";
    public static final String md5Key                  = "35a131404c264f36ca4031500143e4acf0682cd5";
    public static final String tvePlaybackMd5Key       = "A!lu0w6598s389131489HW6oOHls!0s&@88";
    public static final String SOCIAL_SHARE_SECRET_KEY = "2884002a06159742fe68411b366c08847426a12544fbb4a0c0088329eca2b2c4fcdf309e2ab21aec50fd573d6268ab0f2dce2e99c8c2ff34c05387jsj8e8djd8j";
    public static final String DEV_TYPE                = "androidtv";



    public static final String WILLOW_WEB_URL          = "https://ddev.willow.tv/";
    public static final String TVE_AUTH_HEADER_URL     = WILLOW_WEB_URL + "tve_authorization_header";






//    Requests related to the TVE
    public static Map getTVEAuthHeaderParams(String requestUri, String method) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestampString = String.valueOf(timestamp.getTime()*1000);

        String md5BaseString = md5Key + "::" + "webservice" + "::" + timestampString + DEV_TYPE;
        String authToken = generateMD5(md5BaseString);

        Map params = new HashMap();
        params.put("device_type", DEV_TYPE);
        params.put("auth_code", authToken);
        params.put("timestamp", timestampString);
        params.put("request_uri", requestUri);
        params.put("method", method);

        return params;
    }


    public static String getTVEPlaybackEncryptedContentId(String contentId) {
        String md5BaseString = tvePlaybackMd5Key + "" + contentId;
        String encryptedContentId = generateMD5(md5BaseString);

        return encryptedContentId;
    }

    public static String generateMD5(String baseString) {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(baseString.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for(int i =0;i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}