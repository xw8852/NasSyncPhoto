package com.msx7.nas.syncphotos.model


import com.google.gson.annotations.SerializedName

data class AlbumFolderItem(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("owner_user_id")
    var ownerUserId: Int?,
    @SerializedName("parent")
    var parent: Int?,
    @SerializedName("passphrase")
    var passphrase: String?,
    @SerializedName("shared")
    var shared: Boolean?,
    @SerializedName("sort_by")
    var sortBy: String?,
    @SerializedName("sort_direction")
    var sortDirection: String?
)