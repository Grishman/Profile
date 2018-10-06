package com.grishman.profiletest;

import android.app.Application;
import android.os.Build;
import android.support.v7.app.AppCompatDelegate;

/**
 * Application class
 */
public class ProfileTestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initVectorSupport();
    }

    private void initVectorSupport() {
        //use appcompat vector drawables on older devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }
}
