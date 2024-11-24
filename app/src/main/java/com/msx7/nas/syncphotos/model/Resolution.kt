package com.msx7.nas.syncphotos.model


import com.google.gson.annotations.SerializedName

data class Resolution(
    @SerializedName("height")
    var height: Long?,
    @SerializedName("width")
    var width: Long?
)