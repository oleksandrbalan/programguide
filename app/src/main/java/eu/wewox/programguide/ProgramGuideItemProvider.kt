@file:OptIn(ExperimentalFoundationApi::class)

package eu.wewox.programguide

import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.getDefaultLazyLayoutKey
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import eu.wewox.programguide.ProgramGuideItem.Channel
import eu.wewox.programguide.ProgramGuideItem.Program
import eu.wewox.programguide.ProgramGuideItem.Timeline
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.math.max

@Composable
internal fun rememberItemProvider(
    dimensions: ProgramGuideDimensions,
    content: ProgramGuideScope.() -> Unit
): ProgramGuideItemProvider {
    val dimensionsPx = with(LocalDensity.current) { dimensions.roundToPx() }
    val scope = ProgramGuideScopeImpl().apply(content)
    return ProgramGuideItemProvider(
        scope.programs,
        scope.channels,
        scope.timeline,
        dimensionsPx
    )
}

internal class ProgramGuideItemProvider(
    private val programs: List<ProgramGuideItemContent<Program>>,
    private val channels: List<ProgramGuideItemContent<Channel>>,
    private val timeline: List<ProgramGuideItemContent<Timeline>>,
    internal val dimensions: ProgramGuidePxDimensions,
) : LazyLayoutItemProvider {

    val programsBoundsCache: List<Rect> =
        programs.mapIndexed { index, content ->
            content.layoutInfo(index)
                .getBounds(dimensions)
                .translate(
                    translateX = dimensions.channelWidth.toFloat(),
                    translateY = dimensions.timelineHeight.toFloat(),
                )
        }

    val channelsBoundsCache: List<Rect> =
        channels.mapIndexed { index, content ->
            content.layoutInfo(index)
                .getBounds(dimensions)
                .translate(
                    translateX = 0f,
                    translateY = dimensions.timelineHeight.toFloat(),
                )
        }

    val timelineBoundsCache: List<Rect> =
        timeline.mapIndexed { index, content ->
            content.layoutInfo(index)
                .getBounds(dimensions)
                .translate(
                    translateX = dimensions.channelWidth.toFloat(),
                    translateY = 0f,
                )
        }

    override val itemCount: Int =
        programs.size + channels.size + timeline.size

    override fun getContentType(index: Int): Any {
        val (localIndex, list, clazz) = toLocal(index)
        val contentType = list[localIndex].contentType.invoke(localIndex)
        return ContentType(contentType, clazz)
    }

    override fun getKey(index: Int): Any {
        val (localIndex, list, clazz) = toLocal(index)
        val key = list[localIndex].key?.invoke(localIndex) ?: return getDefaultLazyLayoutKey(index)
        return Key(key, clazz)
    }

    @Composable
    override fun Item(index: Int) {
        val (localIndex, list) = toLocal(index)
        list.getOrNull(localIndex)?.item?.invoke(localIndex)
    }

    fun getMaxBounds(size: Size): Rect {
        var maxX = 0f
        var maxY = 0f

        programsBoundsCache.forEach { bounds ->
            maxX = max(maxX, bounds.right)
            maxY = max(maxY, bounds.bottom)
        }

        channelsBoundsCache.forEach { bounds ->
            maxX = max(maxX, bounds.right)
            maxY = max(maxY, bounds.bottom)
        }

        timelineBoundsCache.forEach { bounds ->
            maxX = max(maxX, bounds.right)
            maxY = max(maxY, bounds.bottom)
        }

        return Rect(0f, 0f, maxX - size.width, maxY - size.height)
    }

    fun getPrograms(viewport: Rect): Map<Int, Rect> {
        val result = mutableMapOf<Int, Rect>()
        programsBoundsCache.forEachIndexed { index, bounds ->
            if (bounds.overlaps(viewport)) {
                result[toGlobalIndex<Program>(index)] = bounds
            }
        }
        return result
    }

    fun getChannels(viewport: Rect): Map<Int, Rect> {
        val extendedViewport = viewport.copy(
            left = Float.MIN_VALUE,
            right = Float.MAX_VALUE
        )
        val result = mutableMapOf<Int, Rect>()
        channelsBoundsCache.forEachIndexed { index, bounds ->
            if (bounds.overlaps(extendedViewport)) {
                result[toGlobalIndex<Channel>(index)] = bounds
            }
        }
        return result
    }

    fun getTimeline(viewport: Rect): Map<Int, Rect> {
        val extendedViewport = viewport.copy(
            top = Float.MIN_VALUE,
            bottom = Float.MAX_VALUE
        )

        val result = mutableMapOf<Int, Rect>()
        timelineBoundsCache.forEachIndexed { index, bounds ->
            if (bounds.overlaps(extendedViewport)) {
                result[toGlobalIndex<Timeline>(index)] = bounds
            }
        }
        return result
    }

    private inline fun <reified T : ProgramGuideItem> toGlobalIndex(index: Int): Int {
        var globalIndex = index
        if (T::class == Program::class) {
            return globalIndex
        }
        globalIndex += programs.size
        if (T::class == Channel::class) {
            return globalIndex
        }
        globalIndex += channels.size
        if (T::class == Timeline::class) {
            return globalIndex
        }
        error("Unable to convert $index to global index for ${T::class}")
    }

    private fun toLocal(index: Int): LocalIndex<ProgramGuideItem> {
        var localIndex = index
        if (localIndex < programs.size) {
            return localIndex(localIndex, programs)
        }
        localIndex -= programs.size
        if (localIndex < channels.size) {
            return localIndex(localIndex, channels)
        }
        localIndex -= channels.size
        if (localIndex < timeline.size) {
            return localIndex(localIndex, timeline)
        }
        error("Unable to convert $index to local index")
    }
}

private inline fun <reified T : ProgramGuideItem> localIndex(
    localIndex: Int,
    list: List<ProgramGuideItemContent<T>>
) = LocalIndex(localIndex, list, T::class.simpleName.orEmpty())

private data class LocalIndex<out T : ProgramGuideItem>(
    val localIndex: Int,
    val list: List<ProgramGuideItemContent<T>>,
    val clazz: String,
)

@Parcelize
private data class Key(
    val innerKey: @RawValue Any?,
    val clazz: String,
) : Parcelable

@Parcelize
private data class ContentType(
    val innerContentType: @RawValue Any?,
    val clazz: String,
) : Parcelable
