package eu.wewox.minabox

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
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Creates a [MinaBoxState] that is remembered across compositions.
 *
 * @param initialOffset The lambda to provide initial offset on the plane.
 * @return Instance of the [MinaBoxState].
 */
@Composable
public fun rememberMinaBoxState(
    initialOffset: MinaBoxPositionProvider.() -> Offset = { Offset.Zero }
): MinaBoxState {
    return remember { MinaBoxState(initialOffset) }
}

/**
 * A state object that can be hoisted to control and observe scrolling.
 *
 * @property initialOffset The lambda to provide initial offset on the plane.
 */
@Stable
public class MinaBoxState(
    private val initialOffset: MinaBoxPositionProvider.() -> Offset
) {
    private lateinit var positionProvider: MinaBoxPositionProviderImpl

    private lateinit var _translateX: Animatable<Float, AnimationVector1D>
    private lateinit var _translateY: Animatable<Float, AnimationVector1D>

    /**
     * Offset on the X axis in pixels.
     */
    public val translateX: State<Float> by lazy { _translateX.asState() }

    /**
     * Offset on the Y axis in pixels.
     */
    public val translateY: State<Float> by lazy { _translateY.asState() }

    /**
     * Updates bounds of the layout and initializes the position provider.
     *
     * @param positionProvider An instance of the position provider.
     * @param maxBounds The max size of the layout.
     */
    internal fun updateBounds(
        positionProvider: MinaBoxPositionProviderImpl,
        maxBounds: Rect,
    ) {
        if (!::positionProvider.isInitialized) {
            this.positionProvider = positionProvider

            val (x, y) = positionProvider.initialOffset()

            _translateX = Animatable(x)
            _translateY = Animatable(y)
        }

        _translateX.updateBounds(
            lowerBound = maxBounds.left,
            upperBound = maxBounds.right,
        )
        _translateY.updateBounds(
            lowerBound = maxBounds.top,
            upperBound = maxBounds.bottom,
        )
    }

    /**
     * Translates the current offset by the given value.
     *
     * @param value The value to translate by.
     */
    public suspend fun dragBy(value: Offset) {
        coroutineScope {
            launch {
                _translateX.snapTo(_translateX.value - value.x)
            }
            launch {
                _translateY.snapTo(_translateY.value - value.y)
            }
        }
    }

    /**
     * Animates current offset to the new value.
     *
     * @param x The new offset on the X axis.
     * @param y The new offset on the Y axis.
     */
    public suspend fun animateTo(x: Float = _translateX.value, y: Float = _translateY.value) {
        coroutineScope {
            launch {
                _translateX.animateTo(x)
            }
            launch {
                _translateY.animateTo(y)
            }
        }
    }

    /**
     * Snaps current offset to the new value.
     *
     * @param x The new offset on the X axis.
     * @param y The new offset on the Y axis.
     */
    public suspend fun snapTo(x: Float = _translateX.value, y: Float = _translateY.value) {
        coroutineScope {
            launch {
                _translateX.snapTo(x)
            }
            launch {
                _translateY.snapTo(y)
            }
        }
    }

    /**
     * Flings current offset by the given velocity.
     *
     * @param velocity The velocity to fling by.
     */
    public suspend fun flingBy(velocity: Velocity) {
        coroutineScope {
            launch {
                _translateX.animateDecay(-velocity.x, exponentialDecay())
            }
            launch {
                _translateY.animateDecay(-velocity.y, exponentialDecay())
            }
        }
    }

    /**
     * Stops current offset animations.
     */
    public suspend fun stopAnimation() {
        coroutineScope {
            launch {
                _translateX.stop()
            }
            launch {
                _translateY.stop()
            }
        }
    }

    /**
     * Animates current offset to the item with a given index.
     *
     * @param index The global index of the item.
     * @param alignment The alignment to align item inside the [MinaBox].
     * @param paddingStart An additional start padding to tweak alignment.
     * @param paddingTop An additional top padding to tweak alignment.
     * @param paddingEnd An additional end padding to tweak alignment.
     * @param paddingBottom An additional bottom padding to tweak alignment.
     */
    public suspend fun animateTo(
        index: Int,
        alignment: Alignment = Alignment.Center,
        paddingStart: Float = 0f,
        paddingTop: Float = 0f,
        paddingEnd: Float = 0f,
        paddingBottom: Float = 0f,
    ) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            paddingStart = paddingStart,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
            paddingBottom = paddingBottom,
            currentX = translateX.value,
            currentY = translateY.value
        )
        animateTo(offset.x, offset.y)
    }

    /**
     * Snaps current offset to the item with a given index.
     *
     * @param index The global index of the item.
     * @param alignment The alignment to align item inside the [MinaBox].
     * @param paddingStart An additional start padding to tweak alignment.
     * @param paddingTop An additional top padding to tweak alignment.
     * @param paddingEnd An additional end padding to tweak alignment.
     * @param paddingBottom An additional bottom padding to tweak alignment.
     */
    public suspend fun snapTo(
        index: Int,
        alignment: Alignment = Alignment.Center,
        paddingStart: Float = 0f,
        paddingTop: Float = 0f,
        paddingEnd: Float = 0f,
        paddingBottom: Float = 0f,
    ) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            paddingStart = paddingStart,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
            paddingBottom = paddingBottom,
            currentX = translateX.value,
            currentY = translateY.value
        )
        snapTo(offset.x, offset.y)
    }
}
