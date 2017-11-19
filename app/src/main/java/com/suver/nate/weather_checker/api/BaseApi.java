package com.suver.nate.weather_checker.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by nates on 11/18/2017.
 */

public abstract class BaseApi {
    private static final String LOG = "Api";
    protected JSONObject ExecuteRequest(String url, String content_type,  String parameters) {
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Content-Type", content_type);
            connection.setDoOutput(true);
            if (parameters.length()>0) { //only write parms to the outgoing request if they are specified
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(parameters);
                wr.flush();
                wr.close();
            }

            int responseCode = connection.getResponseCode();
            InputStream stream;
            if (responseCode!=200) { //200 is success, everything else, for this purpose, is not a success.
                stream = connection.getErrorStream();
            }
            else {
                stream = connection.getInputStream();
            }
            return GetJsonObjectFromStream(stream);

        }
        catch (Exception ex) {
            Log.e(LOG,ex.getMessage());
            return getJsonError(ex.getMessage());
        }
    }

    protected JSONObject getJsonError(String errorMessage) {
        String err = "{\'error_description\':\'" + errorMessage + "\'}";
        JSONObject obj=null;
        try {
            obj = new JSONObject(err);
        }
        catch (Exception ex1) {
            Log.e(LOG,ex1.getMessage());
        }
        return obj;
    }

    protected JSONObject GetJsonObjectFromStream(InputStream stream) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(stream));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = input.readLine()) != null) {
                response.append(inputLine);
            }
            input.close();
            return new JSONObject(response.toString());
        } catch (Exception e) {
            Log.e(LOG,e.getMessage());
            return null;
        }
    }

    protected String BuildParm(String value) {
        try {
            String charset = "UTF-8";
            return URLEncoder.encode(value,charset);
        }
        catch(UnsupportedEncodingException ex) {
            Log.e(LOG,ex.getMessage());
            throw new AssertionError("UTF not found on this device");
        }
    }

    protected Bitmap ExecuteBitmapRequest(String url) {
        try {
            URL u = new URL(url);
            Bitmap bmp = BitmapFactory.decodeStream(u.openConnection().getInputStream());
            return bmp;
        }
        catch (Exception ex) {
            Log.e(LOG,ex.getMessage());
            return null;
        }

    }
}
