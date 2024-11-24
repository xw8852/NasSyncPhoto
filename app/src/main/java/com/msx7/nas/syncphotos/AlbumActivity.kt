package com.msx7.nas.syncphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.msx7.nas.syncphotos.data.LocalStorage
import com.msx7.nas.syncphotos.data.getTimeLine
import com.msx7.nas.syncphotos.model.AlbumPhotoItem
import com.msx7.nas.syncphotos.model.AlbumViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumActivity : ComponentActivity() {

    val vm = AlbumViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val focusManager = LocalFocusManager.current
            val scope = rememberCoroutineScope()
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .clickable(indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
            ) { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF84fab0), Color(0xFF8fd3f4))
                            ),
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    InfiniteScrollWaterfallFlow(vm)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            vm.loadData()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun InfiniteScrollWaterfallFlow(
        vm: AlbumViewModel = AlbumViewModel(),
    ) {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        FlowRow(
            modifier = Modifier
                //FlowRow一定是使用verticalScroll横向滚动才有item填充满的效果
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(15.dp),
//            mainAxisSpacing = 8.dp,
//            crossAxisSpacing = 8.dp
        ) {


            vm.photoGroupByMonth.forEach { t ->
                if (vm.photoGroupByMonth.isEmpty()) return
                val info = t.first
                val list = t.second;
                // Date header
                Text(
                    text = info,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.Black
                )

                list.forEach { photo ->
                    val url =
                        "http://117.72.17.135:6102/synofoto/api/v2/p/Thumbnail/get?" +
                                "id=${photo.id}" +
                                "&cache_key=%22${photo.additional?.thumbnail?.cacheKey ?: ""}%22" +
                                "&type=%22unit%22" +
                                "&size=%22sm%22" +
                                "&SynoToken=${LocalStorage.instance.getSyncToken()}&_sid=${LocalStorage.instance.getSID()}"
                    println("url--->" + url);
                    val ratio = photo.additional?.resolution?.let {
                        (it.width?.toFloat() ?: 1.0f) / (it.height?.toFloat() ?: 1.0f)
                    } ?: 1.0f
                    // Replace with actual image loading logic

                    SubcomposeAsyncImage(
                        model =  ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .diskCacheKey(url)
                            .addHeader("x-syno-token", LocalStorage.instance.getSyncToken())
                            .addHeader("Access-Control-Allow-Credentials", "true")
                            .addHeader(
                                "Access-Control-Allow-Orign",
                                "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance"
                            )
                            .addHeader(
                                "referer",
                                "http://117.72.17.135:6102/?launchApp=SYNO.Foto.AppInstance&SynoToken=${LocalStorage.instance.getSyncToken()}"
                            )
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .width(100.dp)
                            .aspectRatio(ratio),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Gray
                                )
                            }
                        },
                        error = {
                            // Handle error state here, e.g., show a placeholder image
                        }
                    )

                }
            }
            Box(modifier = Modifier.padding(vertical = 10.dp)){
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            }

            LaunchedEffect(scrollState) {
                snapshotFlow { scrollState.value }
                    .collect { scrollValue ->
                        if (scrollValue == scrollState.maxValue) {
                            vm.loadMore()
                        }
                    }
            }
        }

    }
}