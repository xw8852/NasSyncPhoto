package com.evyd.http.cookie;

import java.util.List;

import kotlin.jvm.JvmSerializableLambda;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {

    void add(HttpUrl httpUrl, Cookie cookie);

    void add(HttpUrl httpUrl, List<Cookie> cookies);

    List<Cookie> get(HttpUrl httpUrl);

    List<Cookie> getCookies();

    boolean remove(HttpUrl httpUrl, Cookie cookie);

    boolean removeAll();
}