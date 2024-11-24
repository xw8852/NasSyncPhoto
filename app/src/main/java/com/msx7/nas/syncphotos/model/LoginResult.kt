package com.msx7.nas.syncphotos.model

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("did") var did: String?,
    @SerializedName("sid") var sid: String?,
    @SerializedName("synotoken") var synotoken: String?
)