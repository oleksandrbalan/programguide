package eu.wewox.programguide

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * The dimensions of the program guide.
 *
 * Note: If for example channels are not added in the program guide the space for them will be
 * still occupied. In such case you should set [channelWidth] to zero to remove them from layout.
 * The same applies to the top timeline ([timelineHeight]).
 *
 * @property timelineHourWidth
 * @property timelineHeight
 * @property channelWidth
 * @property channelHeight
 * @property currentTimeWidth
 */
public data class ProgramGuideDimensions(
    val timelineHourWidth: Dp,
    val timelineHeight: Dp,
    val channelWidth: Dp,
    val channelHeight: Dp,
    val currentTimeWidth: Dp,
)

/**
 * Converts [ProgramGuideDimensions] to [ProgramGuidePxDimensions].
 */
internal fun ProgramGuideDimensions.roundToPx(density: Density): ProgramGuidePxDimensions =
    with(density) {
        ProgramGuidePxDimensions(
            timelineHourWidth = timelineHourWidth.roundToPx(),
            timelineHeight = timelineHeight.roundToPx(),
            channelWidth = channelWidth.roundToPx(),
            channelHeight = channelHeight.roundToPx(),
            currentTimeWidth = currentTimeWidth.roundToPx(),
        )
    }

/**
 * The dimensions of the program guide in pixels.
 */
internal data class ProgramGuidePxDimensions(
    val timelineHourWidth: Int,
    val timelineHeight: Int,
    val channelWidth: Int,
    val channelHeight: Int,
    val currentTimeWidth: Int,
)
