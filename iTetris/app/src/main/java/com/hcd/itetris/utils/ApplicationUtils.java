package com.hcd.itetris.utils;

import android.app.Application;

/**
 * Created by hcd on 14-11-8.
 * Author:Jvaeyhcd
 */
public class ApplicationUtils extends Application {
    private static ApplicationUtils instance;

    public static ApplicationUtils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
