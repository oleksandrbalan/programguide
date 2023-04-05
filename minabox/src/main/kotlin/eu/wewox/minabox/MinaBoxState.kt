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

@Composable
fun rememberMinaBoxState(initialOffset: MinaBoxPositionProvider.() -> Offset = { Offset.Zero }): MinaBoxState {
    return remember { MinaBoxState(initialOffset) }
}

@Stable
class MinaBoxState(
    private val initialOffset: MinaBoxPositionProvider.() -> Offset
) {
    private lateinit var positionProvider: MinaBoxPositionProviderImpl

    private lateinit var _translateX: Animatable<Float, AnimationVector1D>
    private lateinit var _translateY: Animatable<Float, AnimationVector1D>

    val translateX: State<Float> by lazy { _translateX.asState() }
    val translateY: State<Float> by lazy { _translateY.asState() }

    internal fun updateBounds(
        positionProvider: MinaBoxPositionProviderImpl,
        maxBounds: Rect,
    ) {
        if (::positionProvider.isInitialized) {
            return
        }
        this.positionProvider = positionProvider

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

    suspend fun animateTo(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            currentX = translateX.value,
            currentY = translateY.value
        )
        animateTo(offset.x, offset.y)
    }

    suspend fun snapTo(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            currentX = translateX.value,
            currentY = translateY.value
        )
        snapTo(offset.x, offset.y)
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

    suspend fun stopAnimation() {
        coroutineScope {
            launch {
                _translateX.stop()
            }
            launch {
                _translateY.stop()
            }
        }
    }
}
