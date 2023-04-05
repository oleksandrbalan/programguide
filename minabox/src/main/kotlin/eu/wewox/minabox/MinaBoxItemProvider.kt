@file:OptIn(ExperimentalFoundationApi::class)

package eu.wewox.minabox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.getDefaultLazyLayoutKey
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import eu.wewox.minabox.MinaBoxItem.Value.Absolute
import kotlin.math.max

@Composable
internal fun rememberItemProvider(
    content: MinaBoxScope.() -> Unit
): MinaBoxItemProvider {
    val scope = MinaBoxScopeImpl().apply(content)
    return MinaBoxItemProvider(scope.intervals)
}

internal class MinaBoxItemProvider(
    private val intervals: IntervalList<MinaBoxItemContent>
) : LazyLayoutItemProvider {

    val cache: Map<Int, MinaBoxItem> =
        intervals.mapAll { index, localIndex, item -> index to item.layoutInfo(localIndex) }.toMap()

    val itemsSize: Size by lazy {
        var maxX = 0f
        var maxY = 0f

        cache.forEach { (_, info) ->
            val width = if (info.width is Absolute) info.width.value else 0f
            val height = if (info.height is Absolute) info.height.value else 0f
            maxX = max(maxX, info.offset.x + width)
            maxY = max(maxY, info.offset.y + height)
        }

        Size(maxX, maxY)
    }

    override val itemCount: Int =
        intervals.size

    override fun getContentType(index: Int): Any? =
        withLocalIntervalIndex(index) { localIndex, content ->
            content.contentType.invoke(localIndex)
        }

    override fun getKey(index: Int): Any =
        withLocalIntervalIndex(index) { localIndex, content ->
            content.key?.invoke(localIndex) ?: getDefaultLazyLayoutKey(index)
        }

    @Composable
    override fun Item(index: Int) {
        withLocalIntervalIndex(index) { localIndex, content ->
            content.item.invoke(localIndex)
        }
    }

    fun getItems(translateX: Float, translateY: Float, constraints: Constraints): Map<Int, Rect> {
        val viewport = Rect(
            left = translateX,
            top = translateY,
            right = translateX + constraints.maxWidth,
            bottom = translateY + constraints.maxHeight,
        )

        return cache
            .filterValues { it.overlaps(viewport) }
            .mapValues { (_, info) ->
                info.translate(translateX, translateY, viewport)
            }
    }

    private inline fun <T> withLocalIntervalIndex(
        index: Int,
        block: (localIndex: Int, content: MinaBoxItemContent) -> T
    ): T {
        val interval = intervals[index]
        val localIntervalIndex = index - interval.startIndex
        return block(localIntervalIndex, interval.value)
    }

    private fun <T, R> IntervalList<T>.mapAll(block: (Int, Int, T) -> R): List<R> =
        buildList {
            this@mapAll.forEach { interval ->
                repeat(interval.size) { index ->
                    add(block(index + interval.startIndex, index, interval.value))
                }
            }
        }
}

// TODO: Optimise this
private fun MinaBoxItem.overlaps(other: Rect): Boolean {
    val size = Size(
        width = if (width is Absolute) width.value else other.width,
        height = if (height is Absolute) height.value else other.height,
    )
    val rect = Rect(offset, size)
    return if (lockVertically && lockHorizontally) {
        true
    } else if (lockHorizontally) {
        rect.copy(
            left = Float.MIN_VALUE,
            right = Float.MAX_VALUE,
        ).overlaps(other)
    } else if (lockVertically) {
        rect.copy(
            top = Float.MIN_VALUE,
            bottom = Float.MAX_VALUE,
        ).overlaps(other)
    } else {
        rect.overlaps(other)
    }
}

private fun MinaBoxItem.translate(x: Float, y: Float, viewport: Rect): Rect {
    val translateX = x.takeUnless { lockHorizontally } ?: 0f
    val translateY = y.takeUnless { lockVertically } ?: 0f
    val size = Size(
        width = if (width is Absolute) width.value else viewport.width,
        height = if (height is Absolute) height.value else viewport.height,
    )
    return Rect(offset.copy(x = offset.x - translateX, y = offset.y - translateY), size)
}
