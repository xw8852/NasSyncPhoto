package com.evyd.http.cookie;

import android.util.Log;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {
    private static final String TAG = CookieJarImpl.class.getSimpleName();

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null.");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        printCookies("saveFromResponse", cookies);
        this.cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        printCookies("loadForRequest", this.cookieStore.get(url));
        return this.cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    private void printCookies(String type, List<Cookie> cookie) {
        Log.d(TAG, "printCookies()------>type:" + type + ", cookie:" + cookie.toString());
    }
}