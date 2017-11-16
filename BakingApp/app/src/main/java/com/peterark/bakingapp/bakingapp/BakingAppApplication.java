package com.peterark.bakingapp.bakingapp;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by PETER on 16/11/2017.
 */

public class BakingAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set the Timber
        Timber.plant(new Timber.DebugTree());

    }
}
