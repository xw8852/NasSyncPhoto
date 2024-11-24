package com.msx7.nas.syncphotos

import android.app.Application
import com.evyd.http.cookie.CookieJarImpl
import com.evyd.http.cookie.CookieStore
import com.evyd.http.cookie.PersistentCookieStore
import okhttp3.CookieJar

class PhotoApplication : Application() {

    companion object {
        lateinit var instance: PhotoApplication
            private set
    }

//    lateinit var  cookieJar: CookieJar
//    lateinit var  cookieStore: CookieStore


    override fun onCreate() {
        super.onCreate()
//        cookieStore = PersistentCookieStore(this)
//        cookieJar =  CookieJarImpl(cookieStore)
        instance = this
    }
}