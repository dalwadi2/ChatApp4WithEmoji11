package com.creative.chatapp4;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.quickblox.core.QBSettings;

import java.util.HashMap;
import java.util.Map;

import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.User;
import vc908.stickerfactory.utils.Utils;


public class ApplicationSingleton extends Application {
    private static final String TAG = ApplicationSingleton.class.getSimpleName();

    public static final String APP_ID = "36665";
    public static final String AUTH_KEY = "Rp5OMRbOYZGKa5s";
    public static final String AUTH_SECRET = "gsVK9k9tk5RT9S5";
    public static final String ACCOUNT_KEY = "nhZ7xismqpK1aFDjxxfS";

    public static final String STICKER_API_KEY = "878ef1d920aaf2de14f87483ffc0aed0";

    public static String USER_LOGIN = "dalwadi21";
    public static String USER_PASSWORD = "harsh007";

    private static ApplicationSingleton instance;
    private RequestQueue mRequestQueue;

    public static ApplicationSingleton getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        instance = this;
        // Initialise QuickBlox SDK
        //
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        // Initialise Stickers sdk
        //
        StickersManager.setLoggingEnabled(true);
        StickersManager.initialize(STICKER_API_KEY, this);
        Map<String, String> meta = new HashMap<>();
        meta.put(User.KEY_GENDER, User.GENDER_FEMALE);
        meta.put(User.KEY_AGE, "33");
        StickersManager.setUser(Utils.getDeviceId(this) + "q", meta);
//
//        StickersManager.setUserSubscribed(false);
//

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);
        USER_LOGIN = sharedPreferences.getString("userId", "");
        USER_PASSWORD = sharedPreferences.getString("password", "");
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
