@file:OptIn(ExperimentalFoundationApi::class)
@file:Suppress("all")

package eu.wewox.programguide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProgramGuide(
    modifier: Modifier = Modifier,
    state: ProgramGuideState = rememberProgramGuideState(),
    dimensions: ProgramGuideDimensions = ProgramGuideDefaults.dimensions(),
    content: ProgramGuideScope.() -> Unit
) {
    val itemProvider = rememberItemProvider(dimensions, content)

    var programGuideSize: Size? by remember { mutableStateOf(null) }

    LazyLayout(
        modifier = modifier
            .clipToBounds()
            .lazyLayoutPointerInput(state),
        itemProvider = itemProvider,
    ) { constraints ->
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        if (size != programGuideSize) {
            state.updateBounds(itemProvider, size)
            programGuideSize = size
        }

        val viewport = state.getViewport(constraints)

        val programsPlaceables = itemProvider.getPrograms(viewport).map { (index, bounds) ->
            measure(
                index,
                Constraints.fixed(bounds.width.toInt(), bounds.height.toInt())
            ) to bounds.translate(
                translateX = -state.translateX.value,
                translateY = -state.translateY.value,
            )
        }
        val channelsPlaceables = itemProvider.getChannels(viewport).map { (index, bounds) ->
            measure(
                index,
                Constraints.fixed(bounds.width.toInt(), bounds.height.toInt())
            ) to bounds.translate(
                translateX = 0f,
                translateY = -state.translateY.value,
            )
        }
        val timelinePlaceables = itemProvider.getTimeline(viewport).map { (index, bounds) ->
            measure(
                index,
                Constraints.fixed(bounds.width.toInt(), bounds.height.toInt())
            ) to bounds.translate(
                translateX = -state.translateX.value,
                translateY = 0f,
            )
        }

        val placeables = programsPlaceables + channelsPlaceables + timelinePlaceables

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { (itemPlaceables, position) ->
                itemPlaceables.forEach { placeable ->
                    placeable.placeRelative(
                        x = position.topLeft.x.toInt(),
                        y = position.topLeft.y.toInt(),
                    )
                }
            }
        }
    }
}

private fun Modifier.lazyLayoutPointerInput(state: ProgramGuideState): Modifier =
    pointerInput(Unit) {
        val velocityTracker = VelocityTracker()
        coroutineScope {
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

object ProgramGuideDefaults {

    fun dimensions(): ProgramGuideDimensions =
        ProgramGuideDimensions(
            timelineHourWidth = 128.dp,
            timelineHeight = 32.dp,
            channelWidth = 64.dp,
            channelHeight = 64.dp,
        )
}
