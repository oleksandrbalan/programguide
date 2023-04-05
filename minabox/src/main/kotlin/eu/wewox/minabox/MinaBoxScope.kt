@file:OptIn(ExperimentalFoundationApi::class)

package eu.wewox.minabox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Composable

interface MinaBoxScope {

    fun items(
        count: Int,
        layoutInfo: (index: Int) -> MinaBoxItem,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    fun <T> items(
        items: List<T>,
        layoutInfo: (item: T) -> MinaBoxItem,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ) = items(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )
}

internal class MinaBoxScopeImpl : MinaBoxScope {

    private val _intervals = MutableIntervalList<MinaBoxItemContent>()
    val intervals: IntervalList<MinaBoxItemContent> = _intervals

    override fun items(
        count: Int,
        layoutInfo: (index: Int) -> MinaBoxItem,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        _intervals.addInterval(
            count,
            MinaBoxItemContent(layoutInfo, key, contentType, itemContent)
        )
    }
}

internal class MinaBoxItemContent(
    val layoutInfo: (index: Int) -> MinaBoxItem,
    val key: ((index: Int) -> Any)?,
    val contentType: (index: Int) -> Any?,
    val item: @Composable (index: Int) -> Unit
)
