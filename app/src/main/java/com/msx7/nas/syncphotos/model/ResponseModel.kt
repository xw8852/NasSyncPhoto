package com.msx7.nas.syncphotos.model

import com.google.gson.annotations.SerializedName

class ResponseModel<T> {

    @SerializedName("data")
    var data: T? = null

    @SerializedName("error")
    var error: ErrorCode? = null

    @SerializedName("success")
    var success: Boolean = false

    constructor(data: T?, success: Boolean) {
        this.data = data
        this.success = success
    }

    constructor()

}

data class ErrorCode(@SerializedName("code") var code: Int)

//"error":{"code":119}
