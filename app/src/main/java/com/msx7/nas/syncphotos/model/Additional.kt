package com.msx7.nas.syncphotos.model


import com.google.gson.annotations.SerializedName

data class Additional(
    @SerializedName("orientation")
    var orientation: Long?,
    @SerializedName("orientation_original")
    var orientationOriginal: Long?,
    @SerializedName("resolution")
    var resolution: Resolution?,
    @SerializedName("thumbnail")
    var thumbnail: Thumbnail?
)