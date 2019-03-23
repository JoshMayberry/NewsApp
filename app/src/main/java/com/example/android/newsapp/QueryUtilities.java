package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

//https://xenoblade.fandom.com/api/v1/Articles/List?category=Blades&limit=1000
//https://xenoblade.fandom.com/api/v1/Articles/List?category=XC2_Locations&limit=1000
//https://xenoblade.fandom.com/api/v1/Articles/List?category=XC2_Items&limit=1000

/**
 * A utility class that is used to get information from the internet.
 * See: http://www.wikia.com/api/v1#!/Articles/getList_get_2
 * Use: https://gist.github.com/udacityandroid/10892631f57f9f073ab9e1d11cfaafcf
 */
class QueryUtilities {
    private static final String LOG_TAG = QueryUtilities.class.getSimpleName();

    private QueryUtilities() {
    }

    /**
     * Formats a string as a URL object.
     */
    static URL createUrl(String stringUrl) {
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException error) {
            Log.e(LOG_TAG, "Error with creating URL ", error);
            return null;
        }
    }

    /**
     * Makes an HTTP request to the given URL and return a String as the response.
     */
    static String makeHttpRequest(String requestUrl) throws IOException {
        if (TextUtils.isEmpty(requestUrl)) {
            return "";
        }

        String jsonResponse = "";
        URL url = createUrl(requestUrl);
        if (url == null) {
            return "";
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException error) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", error);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    static String readFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        StringBuilder output = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }
}