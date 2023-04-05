@file:OptIn(ExperimentalFoundationApi::class)

package eu.wewox.minabox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun MinaBox(
    modifier: Modifier = Modifier,
    state: MinaBoxState = rememberMinaBoxState(),
    alignmentPadding: MinaBoxAlignmentPadding = MinaBoxAlignmentPadding(),
    content: MinaBoxScope.() -> Unit
) {
    val itemProvider = rememberItemProvider(content)

    LazyLayout(
        modifier = modifier
            .clipToBounds()
            .lazyLayoutPointerInput(state),
        itemProvider = itemProvider,
    ) { constraints ->
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val positionProvider = MinaBoxPositionProviderImpl(itemProvider, alignmentPadding, size)
        val maxSize = itemProvider.itemsSize
        val bounds = Rect(
            left = 0f,
            top = 0f,
            right = (maxSize.width - size.width).coerceAtLeast(0f),
            bottom = (maxSize.height - size.height).coerceAtLeast(0f)
        )
        state.updateBounds(positionProvider, bounds)

        val items = itemProvider.getItems(
            state.translateX.value,
            state.translateY.value,
            constraints,
        )

        val placeables = items.map { (index, bounds) ->
            measure(
                index,
                Constraints.fixed(bounds.width.toInt(), bounds.height.toInt())
            ) to bounds.topLeft
        }

        val width = min(maxSize.width.toInt(), constraints.maxWidth)
        val height = min(maxSize.height.toInt(), constraints.maxHeight)

        layout(width, height) {
            placeables.forEach { (itemPlaceables, position) ->
                itemPlaceables.forEach { placeable ->
                    placeable.placeRelative(
                        x = position.x.toInt(),
                        y = position.y.toInt(),
                    )
                }
            }
        }
    }
}

private fun Modifier.lazyLayoutPointerInput(state: MinaBoxState): Modifier =
    pointerInput(Unit) {
        val velocityTracker = VelocityTracker()
        coroutineScope {
            launch {
                detectTapGestures(
                    onPress = { state.stopAnimation() }
                )
            }

            detectDragGestures(
                onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity()
                    launch {
                        state.flingBy(velocity)
                    }
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                    launch {
                        state.dragBy(dragAmount)
                    }
                }
            )
        }
    }
