package com.example.android.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

/**
 * A utility class that is used to get information from the internet.
 * Use: https://gist.github.com/udacityandroid/10892631f57f9f073ab9e1d11cfaafcf
 */
public class QueryUtilities {
    private static final String LOG_TAG = QueryUtilities.class.getSimpleName();

    private QueryUtilities() {
    }

    /**
     * Formats a string as a URL object.
     */
	public static URL createUrl(String stringUrl) {
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException error) {
            Log.e(LOG_TAG, "Error with creating URL ", error);
            return null;
        }
    }

    /**
     * Formats a string as a URI object.
     */
	public static Uri createUri(String stringUri) {
        if (stringUri.isEmpty()) {
            return null;
        }
        return Uri.parse(stringUri);
    }

    /**
     * Makes an HTTP request to the given URL and return a String as the response.
     */
	public static String makeHttpRequest(String requestUrl) throws IOException {
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
	public static String readFromStream(InputStream inputStream) throws IOException {
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

	/**
	 * Checks internet connectivity.
	 * See: https://developer.android.com/training/basics/network-ops/connecting.html
	 * Use: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#DetermineConnection
	 */
	public static boolean checkOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}