/*
 * Copyright 2021 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.burnoutcrew.lazylistscrolleffectssample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.util.DebugLogger
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.coil.LocalImageLoader
import io.burnoutcrew.lazylistscrolleffectssample.ui.theme.LazyListScrollEffectsSampleTheme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    private val imageLoader by lazy {
        ImageLoader.Builder(applicationContext)
            .allowRgb565(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                MainScreen(data = items)
            }
        }
    }

    companion object {
        private val imageUrls = listOf(
            "https://picsum.photos/id/1025/4951/3301",
            "https://picsum.photos/id/1012/3973/2639",
            "https://picsum.photos/id/102/4320/3240",
            "https://picsum.photos/id/1004/5616/3744",
            "https://picsum.photos/id/1011/5472/3648",
            "https://picsum.photos/id/1019/5472/3648",
            "https://picsum.photos/id/187/4000/2667",
            "https://picsum.photos/id/1020/4288/2848",
            "https://picsum.photos/id/1021/2048/1206",
            "https://picsum.photos/id/1024/1920/1280",
            "https://picsum.photos/id/1013/4256/2832"
        )
        private val items = imageUrls.mapIndexed { i, s -> ImageItem(i, "Title $i", s) }
    }
}

@Preview("ImageCard")
@Composable
fun ImageCardPreview() {
    LazyListScrollEffectsSampleTheme {
        ImageCard(item = ImageItem(0, "Sample", ""), modifier = Modifier.height(250.dp))
    }
}

@Preview("MainScreen", showBackground = true)
@Composable
fun MainScreenPreview() {
    LazyListScrollEffectsSampleTheme {
        MainScreen(data = List(5) { ImageItem(it, "Sample $it", "") })
    }
}

@Composable
private fun MainScreen(data: List<ImageItem>) {
    val state = rememberLazyListState()
    LazyListScrollEffectsSampleTheme {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            item { ScaleFadeImageCardRow(data) }
            items(2) { ImageCardRow(data) }
            items(data, { it.id }) { MaxWidthImageCard(state, it) }
            items(2) { ImageCardRow(data) }
        }
    }
}

@Composable
private fun ImageCardRow(items: List<ImageItem>) {
    val state: LazyListState = rememberLazyListState()
    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ImageCard(
                item,
                modifier = Modifier.size(160.dp, 140.dp),
                imageModifier = Modifier
                    .requiredWidth(220.dp)
                    .graphicsLayer {
                        translationX = state.layoutInfo.normalizedItemPosition(item.id) * 30
                    }
            )
        }
    }
}

@Composable
private fun MaxWidthImageCard(state: LazyListState, item: ImageItem) {
    ImageCard(
        item,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 16.dp, end = 16.dp),
        imageModifier = Modifier
            .requiredHeight(350.dp)
            .graphicsLayer {
                translationY = state.layoutInfo.normalizedItemPosition(item.id) * 50
            },
    )

}

@Composable
private fun ScaleFadeImageCardRow(items: List<ImageItem>) {
    val state: LazyListState = rememberLazyListState()
    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ImageCard(
                item,
                modifier = Modifier
                    .size(160.dp, 140.dp)
                    .graphicsLayer {
                        val value = 1 - (state.layoutInfo.normalizedItemPosition(item.id).absoluteValue * 0.15F)
                        alpha = value
                        scaleX = value
                        scaleY = value
                    },
                imageModifier = Modifier.requiredWidth(220.dp)
            )
        }
    }
}

@Composable
private fun ImageCard(item: ImageItem, modifier: Modifier = Modifier, imageModifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Box {
            CoilImage(
                data = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = imageModifier,
                loading = {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 1f to Color.Black))
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 32.dp),
                color = Color.White,
                maxLines = 1,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}