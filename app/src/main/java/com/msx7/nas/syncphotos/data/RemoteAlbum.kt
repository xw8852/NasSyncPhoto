package com.msx7.nas.syncphotos.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.msx7.nas.syncphotos.model.AlbumPhotoItem
import com.msx7.nas.syncphotos.model.AlbumPhotoList
import com.msx7.nas.syncphotos.model.AlbumSection
import com.msx7.nas.syncphotos.model.ResponseModel
import com.msx7.nas.syncphotos.net.NetWork
import com.msx7.nas.syncphotos.net.await
import kotlinx.coroutines.Dispatchers
import okhttp3.FormBody
import okhttp3.Request
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

data class TimeLineInfo(var start: Long, var end: Long, var limit: Int);


suspend fun getTimeLine(): List<TimeLineInfo> {
    return with(Dispatchers.IO) {
        println("start ---> getTimeLine ${Thread.currentThread().name}")
        val response = NetWork.instance.client.newCall(
            Request.Builder()
                .url("http://117.72.17.135:6102/webapi/entry.cgi/SYNO.Foto.Browse.Timeline")
                ///SYNO.Foto.Browse.Timeline&api=SYNO.Foto.Browse.Timeline&method=get&version=5&timeline_group_unit=%22day%22
                .addHeader("ContentType", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("x-syno-token", LocalStorage.instance.getSyncToken())
                .addHeader("referer", "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance")
                .post(
                    FormBody.Builder()
                        .add("api", "SYNO.Foto.Browse.Timeline")
                        .add("method", "get")
                        .add("version", "5")
//                        .add("SynoToken", LocalStorage.instance.getSyncToken()?:"")
//                        .add("timeline_group_unit", "day")
                        .build()
                )
                .build()
        ).await()
        val result = response.body?.byteString()?.string(StandardCharsets.UTF_8)
        println("getTimeLine ---> $result")

        val data = Gson().fromJson<ResponseModel<AlbumSection>>(
            result, object : TypeToken<ResponseModel<AlbumSection>>() {}.type
        )
        if (!data.success) {
            logoutByError();
            return emptyList()
        }
        val sdf = SimpleDateFormat("yyyy-M-d")
        return data?.data?.list?.stream()?.map { f ->
            val dates = f.list.map { k ->
                sdf.parse(
                    "${k.year}-${k.month}-${k.day}"
                ).time
            }
            val min = dates.stream().min { o1, o2 -> o1.compareTo(o2) }.get()
            val max = dates.stream().max { o1, o2 -> o1.compareTo(o2) }.get()
            println("start = ${Date(min).toLocaleString()} end = ${Date(max).toLocaleString()}")
            TimeLineInfo(min / 1000, (max + TimeUnit.DAYS.toMillis(1)) / 1000, f.limit)
        }?.collect(Collectors.toCollection { java.util.ArrayList() }) ?: emptyList()
    }
}

fun logoutByError() {
//    LocalStorage.instance.saveLoginState(false)
////    PhotoApplication.instance.cookieStore.removeAll()
//    PhotoApplication.instance.apply {
//        startActivity(Intent(this, MainActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        })
//    }
}

suspend fun getPhotoItemInfo(timeline: TimeLineInfo): List<AlbumPhotoItem> {
    return with(Dispatchers.IO) {
        println("start ---> getPhotoItemInfo ${Thread.currentThread().name}")
        val response = NetWork.instance.client.newCall(
            Request.Builder()
                .url("http://117.72.17.135:6102//webapi/entry.cgi/SYNO.Foto.Browse.Item")
                .addHeader("ContentType", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("x-syno-token", LocalStorage.instance.getSyncToken())
                .addHeader("referer", "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance")
                .post(
                    FormBody.Builder()
                        .add("api", "SYNO.Foto.Browse.Item")
                        .add("method", "list")
                        .add("version", "4")
                        .add("offset", "0")
                        .add("limit", "${timeline.limit}")
                        .add("start_time", "${timeline.start}")
                        .add("end_time", "${timeline.end}")
//                        .add("SynoToken", LocalStorage.instance.getSyncToken()?:"")
                        .add(
                            "additional",
                            " [\"thumbnail\",\"resolution\",\"orientation\",\"video_convert\",\"video_meta\",\"address\"]"
                        )
                        .add("timeline_group_unit", "day")
                        .build()
                )
                .build()
        ).await()
        val result = response.body?.byteString()?.string(StandardCharsets.UTF_8)
        val data = Gson().fromJson<ResponseModel<AlbumPhotoList>>(
            result, object : TypeToken<ResponseModel<AlbumPhotoList>>() {}.type
        )
        println("getPhotoItemInfo ---> $result")
        if (!data.success) {
            logoutByError()
            return emptyList()
        }
        return data?.data?.list ?: emptyList()
    }
}

suspend fun getPhotoItemInfo(limit: Int, folderId: Long) {
    with(Dispatchers.IO) {
        val response = NetWork.instance.client.newCall(
            Request.Builder()
                .url("http://117.72.17.135:6102//webapi/entry.cgi/SYNO.Foto.Browse.Item")
                .addHeader("ContentType", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("x-syno-token", LocalStorage.instance.getSyncToken())
                .addHeader("referer", "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance")
                .post(
                    FormBody.Builder()
                        .add("api", "SYNO.Foto.Browse.Item")
                        .add("method", "list")
                        .add("version", "4")
                        .add("offset", "asc")
                        .add("sort_by", "takentime")
                        .add("sort_direction", "0")
                        .add("limit", "$limit")
                        .add("folder_id", "$folderId")
//                        .add("SynoToken", LocalStorage.instance.getSyncToken()?:"")
                        .add(
                            "additional",
                            " [\"thumbnail\",\"resolution\",\"orientation\",\"video_convert\",\"video_meta\"]"
                        )
                        .build()
                )
                .build()
        ).await()
    }
}

suspend fun syncToken(): String? {
    return with(Dispatchers.IO) {
        println("start ---> syncToken ${Thread.currentThread().name}")
        val response = NetWork.instance.client.newCall(
            Request.Builder()
                .url("http://117.72.17.135:6102/webapi/entry.cgi/SYNO.API.Auth")
                .addHeader("ContentType", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("x-syno-token", LocalStorage.instance.getSyncToken())
                .addHeader("referer", "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance")
                .post(
                    FormBody.Builder()
                        .add("api", "SYNO.API.Auth")
                        .add("method", "token")
                        .add("version", "6")
                        .add("updateSynoToken", "true")
//                        .add("SynoToken", LocalStorage.instance.getSyncToken())
                        .build()
                )
                .build()
        ).await()
        val result = response.body?.byteString()?.string(StandardCharsets.UTF_8)
        val data = Gson().fromJson<ResponseModel<SyncToken>>(
            result, object : TypeToken<ResponseModel<SyncToken>>() {}.type
        )
        println("synotoken ---> $result")
        return data.data?.synotoken
    }
}


data class SyncToken(@SerializedName("synotoken") var synotoken: String);