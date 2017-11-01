package com.peterark.bakingapp.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import timber.log.Timber;

/**
 * Created by PETER on 29/10/2017.
 */

public class NetworkUtils {

    public final static int LOAD_RECIPE_LIST = 1001;

    public static String BAKING_RECIPE_WS_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static URL buildUrl(int requestMode) {

        // Init Base Search URL.
        String baseSearchUrl = "";
        switch (requestMode){
            case LOAD_RECIPE_LIST:
                baseSearchUrl = BAKING_RECIPE_WS_URL;
                break;
        }

        if (baseSearchUrl.length()==0) return null;


        // Began to build the Uri with base url.
        Uri builtUri = Uri.parse(baseSearchUrl).buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timber.d("Built URI " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     * Source:  Udacity Sunshine Project
     */
    public static String getResponseFromHttpUrl(Context context, URL url) throws Exception {

        if(!isOnline(context))
            return null;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // Taken from StackOverflow post. https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    private static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
