@file:Suppress("all")

package eu.wewox.programguide

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberProgramGuideState(initialOffset: ProgramGuidePositionProvider.() -> Offset = { Offset.Zero }): ProgramGuideState {
    return remember { ProgramGuideState(initialOffset) }
}

@Stable
class ProgramGuideState(
    private val initialOffset: ProgramGuidePositionProvider.() -> Offset
) {
    private lateinit var positionProvider: PositionProvider

    private var itemProvider: ProgramGuideItemProvider? = null
    private var size: Size? = null

    private lateinit var _translateX: Animatable<Float, AnimationVector1D>
    private lateinit var _translateY: Animatable<Float, AnimationVector1D>

//    private val _translateX: Animatable<Float, AnimationVector1D> = Animatable(0f)
//    private val _translateY: Animatable<Float, AnimationVector1D> = Animatable(0f)

    val translateX: State<Float> by lazy { _translateX.asState() }
    val translateY: State<Float> by lazy { _translateY.asState() }

    internal fun updateBounds(
        itemProvider: ProgramGuideItemProvider,
        size: Size,
    ) {
        positionProvider = PositionProvider(itemProvider, size)

        val maxBounds = itemProvider.getMaxBounds(size)

        val (x, y) = positionProvider.initialOffset()

        _translateX = Animatable(x)
        _translateY = Animatable(y)

        _translateX.updateBounds(
            lowerBound = maxBounds.left,
            upperBound = maxBounds.right,
        )
        _translateY.updateBounds(
            lowerBound = maxBounds.top,
            upperBound = maxBounds.bottom,
        )
    }

    suspend fun dragBy(value: Offset) {
        coroutineScope {
            launch {
                _translateX.snapTo(_translateX.value - value.x)
            }
            launch {
                _translateY.snapTo(_translateY.value - value.y)
            }
        }
    }

    suspend fun animateTo(x: Float = _translateX.value, y: Float = _translateY.value) {
        coroutineScope {
            launch {
                _translateX.animateTo(x)
            }
            launch {
                _translateY.animateTo(y)
            }
        }
    }

    suspend fun snapTo(x: Float = _translateX.value, y: Float = _translateY.value) {
        coroutineScope {
            launch {
                _translateX.snapTo(x)
            }
            launch {
                _translateY.snapTo(y)
            }
        }
    }

    suspend fun animateToProgram(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getProgramOffset(index, alignment)
        animateTo(offset.x, offset.y)
    }

    suspend fun snapToProgram(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getProgramOffset(index, alignment)
        snapTo(offset.x, offset.y)
    }

    suspend fun animateToChannel(
        index: Int,
        alignment: Alignment.Vertical = Alignment.CenterVertically
    ) {
        val position = positionProvider.getChannelPosition(index, alignment)
        animateTo(translateX.value, position)
    }

    suspend fun snapToChannel(
        index: Int,
        alignment: Alignment.Vertical = Alignment.CenterVertically
    ) {
        val position = positionProvider.getChannelPosition(index, alignment)
        snapTo(translateX.value, position)
    }

    suspend fun flingBy(velocity: Velocity) {
        coroutineScope {
            launch {
                _translateX.animateDecay(-velocity.x, exponentialDecay())
            }
            launch {
                _translateY.animateDecay(-velocity.y, exponentialDecay())
            }
        }
    }

    fun getViewport(constraints: Constraints): Rect {
        val x = translateX.value
        val y = translateY.value
        return Rect(
            left = x,
            top = y,
            right = x + constraints.maxWidth,
            bottom = y + constraints.maxHeight,
        )
    }

    private class PositionProvider(
        private val itemProvider: ProgramGuideItemProvider,
        private val size: Size,
    ) : ProgramGuidePositionProvider {

        override fun getProgramOffset(index: Int, alignment: Alignment): Offset {
            val dimensions = itemProvider.dimensions
            val bounds = itemProvider.programsBoundsCache[index]
            val offset = alignment.align(
                IntSize(bounds.width.toInt(), bounds.height.toInt()),
                IntSize(
                    size.width.toInt() - dimensions.channelWidth,
                    size.height.toInt() - dimensions.timelineHeight
                ),
                LayoutDirection.Ltr // TODO: Add support for RTL
            )
            val x = bounds.left - offset.x - dimensions.channelWidth
            val y = bounds.top - offset.y - dimensions.timelineHeight
            return Offset(x, y)
        }

        override fun getChannelPosition(index: Int, alignment: Alignment.Vertical): Float {
            val dimensions = itemProvider.dimensions
            val bounds = itemProvider.channelsBoundsCache[index]
            val position = alignment.align(
                bounds.height.toInt(),
                size.height.toInt() - dimensions.timelineHeight,
            )
            return bounds.top - position - dimensions.timelineHeight
        }

        override fun getTimelinePosition(index: Int, alignment: Alignment.Horizontal): Float {
            val dimensions = itemProvider.dimensions
            val bounds = itemProvider.timelineBoundsCache[index]
            val position = alignment.align(
                bounds.width.toInt(),
                size.width.toInt() - dimensions.channelWidth,
                LayoutDirection.Ltr // TODO: Add support for RTL
            )
            return bounds.left - position - dimensions.channelWidth
        }
    }
}

interface ProgramGuidePositionProvider {
    fun getProgramOffset(index: Int, alignment: Alignment): Offset
    fun getChannelPosition(index: Int, alignment: Alignment.Vertical): Float
    fun getTimelinePosition(index: Int, alignment: Alignment.Horizontal): Float
}
