package com.msx7.nas.syncphotos.net

import android.util.Log
import com.evyd.http.cookie.CookieJarImpl
import com.evyd.http.cookie.PersistentCookieStore
import com.msx7.nas.syncphotos.PhotoApplication
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class NetWork {
    private constructor()

    var client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(CookieJarImpl(PersistentCookieStore(PhotoApplication.instance)))
        .retryOnConnectionFailure(true)
        .callTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
//        .cache(
//            Cache(
//                File(PhotoApplication.instance.cacheDir, "http_cache"),
//                (1024 * 1024 * 50).toLong()
//            )
//        )
        .addInterceptor(HttpLoggingInterceptor({ msg ->
            println(msg)
            Log.e("network", msg)
        }))
        .build()
        private set

    companion object {
        val instance: NetWork by lazy { NetWork() }
    }

    suspend fun login(username: String, password: String): String? {
        val response = client.newCall(
            Request.Builder()
                .url("http://117.72.17.135:6102/webapi/entry.cgi?api=SYNO.API.Auth&version=6&enable_syno_token=yes&method=login&account=${username}&passwd=${password}")
                .get()
                .build()
        ).await()
        val result = response.body?.byteString()?.string(StandardCharsets.UTF_8)
        println("login --------> $result")
        return result
    }
}

public suspend fun Call.await(): Response {
    val callStack = IOException().apply {
        // Remove unnecessary lines from stacktrace
        // This doesn't remove await$default, but better than nothing
        stackTrace = stackTrace.copyOfRange(1, stackTrace.size)
    }
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                callStack?.initCause(e)
                continuation.resumeWithException(callStack ?: e)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
        }
    }
}