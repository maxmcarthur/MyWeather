package com.example.maxmcarthur.myweather;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * AppSingleton:
 *
 * Class in order to handle Volley JSON Requests.
 */

public class AppSingleton {
    private static AppSingleton mSingletonInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    private AppSingleton(Context c) {
        this.mContext = c;
    }

    public static synchronized  AppSingleton newInstance(Context c) {
        if (mSingletonInstance == null) {
            mSingletonInstance = new AppSingleton(c);
        }
        return mSingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void addRequest(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void clearRequestTag(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
