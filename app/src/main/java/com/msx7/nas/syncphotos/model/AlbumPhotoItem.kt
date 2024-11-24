package com.msx7.nas.syncphotos.model


import com.google.gson.annotations.SerializedName

data class AlbumPhotoItem(
    @SerializedName("additional")
    var additional: Additional?,
    @SerializedName("filename")
    var filename: String?,
    @SerializedName("filesize")
    var filesize: Long?,
    @SerializedName("folder_id")
    var folderId: Long?,
    @SerializedName("id")
    var id: Long?,
    @SerializedName("indexed_time")
    var indexedTime: Long?,
    @SerializedName("owner_user_id")
    var ownerUserId: Long?,
    @SerializedName("time")
    var time: Long?,
    @SerializedName("type")
    var type: String?
)

data class AlbumPhotoList(
    @SerializedName("list")
    var list: List<AlbumPhotoItem>?
)