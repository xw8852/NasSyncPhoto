package com.msx7.nas.syncphotos.model

import com.google.gson.annotations.SerializedName

data class AlbumSection(
    @SerializedName("section") var list: List<AlbumSectionList>
)

data class AlbumSectionList(
    @SerializedName("limit") var limit: Int,
    @SerializedName("list") var list: List<AlbumSectionItem>
)

data class AlbumSectionItem(
    @SerializedName("day") var day: Int,
    @SerializedName("item_count") var itemCount: Int,
    @SerializedName("month") var month: Int,
    @SerializedName("year") var year: Int,
)