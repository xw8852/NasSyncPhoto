package com.msx7.nas.syncphotos.model

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.msx7.nas.syncphotos.AlbumActivity
import com.msx7.nas.syncphotos.data.LocalStorage
import com.msx7.nas.syncphotos.net.NetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LoginBean(var userName: String = "", var password: String = "")
class LoginModel : ViewModel() {
    var isLogin by mutableStateOf(false)
        private set
    var loginUiState by mutableStateOf(LoginBean())
        private set

    fun updateUserName(userName: String) {
        loginUiState = loginUiState.copy(userName = userName)
    }

    fun updatePassword(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }


    suspend fun login(context: Context) {
        isLogin = true
        if (getLoginResult()) {
            LocalStorage.instance.saveLoginState(true)

            context.startActivity(Intent(context, AlbumActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            return
        }
        Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show()
        isLogin = false
    }

    private suspend fun getLoginResult(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = NetWork.instance.login(loginUiState.userName, loginUiState.password)
                val result = Gson().fromJson<ResponseModel<LoginResult>>(
                    response, object : TypeToken<ResponseModel<LoginResult>>() {}.type
                )
                LocalStorage.instance.saveSyncToken(result.data?.synotoken ?: "")
                LocalStorage.instance.saveSID(result.data?.sid ?: "")
                return@withContext result.success
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

}
