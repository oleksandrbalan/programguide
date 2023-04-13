@file:Suppress("all")

package eu.wewox.programguide

import androidx.compose.runtime.Composable
import eu.wewox.programguide.ProgramGuideItem.Channel
import eu.wewox.programguide.ProgramGuideItem.Program
import eu.wewox.programguide.ProgramGuideItem.Timeline

interface ProgramGuideScope {

    fun programs(
        count: Int,
        layoutInfo: (index: Int) -> Program,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    fun channels(
        count: Int,
        layoutInfo: (index: Int) -> Channel,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    fun timeline(
        count: Int,
        layoutInfo: (index: Int) -> Timeline,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    fun <T> programs(
        items: List<T>,
        layoutInfo: (item: T) -> Program,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ) = programs(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )

    fun <T> channels(
        items: List<T>,
        layoutInfo: (item: T) -> Channel,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ) = channels(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )

    fun <T> timeline(
        items: List<T>,
        layoutInfo: (item: T) -> Timeline,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ) = timeline(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )
}

internal class ProgramGuideScopeImpl : ProgramGuideScope {

    private val _programs = mutableListOf<ProgramGuideItemContent<Program>>()
    val programs: List<ProgramGuideItemContent<Program>> get() = _programs

    private val _channels = mutableListOf<ProgramGuideItemContent<Channel>>()
    val channels: List<ProgramGuideItemContent<Channel>> get() = _channels

    private val _timeline = mutableListOf<ProgramGuideItemContent<Timeline>>()
    val timeline: List<ProgramGuideItemContent<Timeline>> get() = _timeline

    override fun programs(
        count: Int,
        layoutInfo: (index: Int) -> Program,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) = _programs.replace(count, layoutInfo, key, contentType, itemContent)

    override fun channels(
        count: Int,
        layoutInfo: (index: Int) -> Channel,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) = _channels.replace(count, layoutInfo, key, contentType, itemContent)

    override fun timeline(
        count: Int,
        layoutInfo: (index: Int) -> Timeline,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) = _timeline.replace(count, layoutInfo, key, contentType, itemContent)

    private fun <T : ProgramGuideItem> MutableList<ProgramGuideItemContent<T>>.replace(
        count: Int,
        layoutInfo: (index: Int) -> T,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        clear()
        addAll(
            List(count) { ProgramGuideItemContent(layoutInfo, key, contentType, itemContent) }
        )
    }
}

internal class ProgramGuideItemContent<out T : ProgramGuideItem>(
    val layoutInfo: (index: Int) -> T,
    val key: ((index: Int) -> Any)?,
    val contentType: (index: Int) -> Any?,
    val item: @Composable (index: Int) -> Unit
)
