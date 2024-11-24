package com.msx7.nas.syncphotos.model


import com.google.gson.annotations.SerializedName

data class Thumbnail(
    @SerializedName("cache_key")
    var cacheKey: String?,
    @SerializedName("m")
    var m: String?,
    @SerializedName("preview")
    var preview: String?,
    @SerializedName("sm")
    var sm: String?,
    @SerializedName("unit_id")
    var unitId: Long?,
    @SerializedName("xl")
    var xl: String?
)