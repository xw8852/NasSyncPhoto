package com.msx7.nas.syncphotos.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.msx7.nas.syncphotos.data.LocalStorage
import com.msx7.nas.syncphotos.data.TimeLineInfo
import com.msx7.nas.syncphotos.data.getPhotoItemInfo
import com.msx7.nas.syncphotos.data.getTimeLine
import com.msx7.nas.syncphotos.data.logoutByError
import com.msx7.nas.syncphotos.data.syncToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class AlbumViewModel : ViewModel() {
    private var photoList = mutableListOf<AlbumPhotoItem>()

    var photoGroupByMonth by mutableStateOf(mutableListOf<Pair<String, List<AlbumPhotoItem>>>())
        private set
    var syncToken = ""
        private set
    private val timeLine = mutableListOf<TimeLineInfo>()

    public fun loadMore() {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        CoroutineScope(Dispatchers.IO).launch {
            if(timeLine.isEmpty())return@launch
            val timeline = timeLine.removeAt(0)
            val time = getPhotoItemInfo(timeline)
            if (time.isNotEmpty()) {
                photoList.addAll(time)
                photoGroupByMonth = photoList.groupBy { photo ->
                    // Convert the Unix timestamp to a Date object
                    val date =
                        Date((photo.time ?: 0L) * 1000) // Convert seconds to milliseconds
                    // Format the date as "年-月-日"
                    sdf.format(date)
                }.map { (date, photos) ->
                    // Convert the map entry to a Pair
                    Pair(date, photos)
                }.toMutableList()
                return@launch
            }
        }
    }

    suspend fun loadData() {
        syncToken = LocalStorage.instance.getSyncToken()
        if (syncToken?.isNotEmpty() != true) {
            logoutByError()
            return
        }
        val data = syncToken()
        syncToken = data ?: syncToken
        LocalStorage.instance.saveSyncToken(syncToken)
        CoroutineScope(Dispatchers.IO).launch {
            val data = getTimeLine();
            if (data.isEmpty()) return@launch
            timeLine.addAll(data)
            loadMore()

        }

    }
}
