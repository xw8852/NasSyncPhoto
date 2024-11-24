package com.msx7.nas.syncphotos.data

import android.annotation.SuppressLint
import android.content.Context
import com.msx7.nas.syncphotos.PhotoApplication

class LocalStorage {

    private constructor()

    companion object {
        val instance by lazy { LocalStorage() }
    }

    private val sp =
        PhotoApplication.instance.getSharedPreferences("data_cache", Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun saveLoginState(loginSuccess: Boolean) {
        sp.edit().putBoolean("login_success", loginSuccess).apply()
    }

    fun isLogin(): Boolean {
        return sp.getBoolean("login_success", false)
    }

    private var synotoken: String = ""

    @SuppressLint("CommitPrefEdits")
    fun saveSyncToken(synotoken: String) {
        this.synotoken = synotoken;
        sp.edit().putString("synotoken", synotoken).apply()
    }

    fun getSyncToken(): String {
        if (synotoken.isNotEmpty()) return synotoken
        synotoken = sp.getString("synotoken", "") ?: ""
        return synotoken;
    }

    private var sid: String = "";
    fun saveSID(sid: String) {
        this.sid = sid;
        sp.edit().putString("sid", sid).apply()
    }

    fun getSID(): String {
        if (sid.isNotEmpty()) return sid;
        sid = sp.getString("sid", sid) ?: sid
        return sid;
    }


}