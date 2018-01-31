package com.example.a85625.seoultour;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by 85625 on 2017-10-27.
 */

public class seoultour extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base)
    {
        //android:name="android.support.multidex.MultiDexApplication"
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
