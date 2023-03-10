package eu.wewox.programguide

import androidx.compose.ui.geometry.Rect

sealed interface ProgramGuideItem {

    data class Timeline(
        val startHour: Float,
        val endHour: Float,
    ) : ProgramGuideItem

    data class Channel(
        val index: Int,
    ) : ProgramGuideItem

    data class Program(
        val channelIndex: Int,
        val startHour: Float,
        val endHour: Float,
    ) : ProgramGuideItem
}

internal fun ProgramGuideItem.Program.getBounds(dimensions: ProgramGuidePxDimensions): Rect =
    Rect(
        left = startHour * dimensions.timelineHourWidth,
        top = channelIndex.toFloat() * dimensions.channelHeight,
        right = endHour * dimensions.timelineHourWidth,
        bottom = (channelIndex.toFloat() + 1) * dimensions.channelHeight,
    )

internal fun ProgramGuideItem.Channel.getBounds(dimensions: ProgramGuidePxDimensions): Rect =
    Rect(
        left = 0f,
        top = index.toFloat() * dimensions.channelHeight,
        right = dimensions.channelWidth.toFloat(),
        bottom = (index.toFloat() + 1) * dimensions.channelHeight,
    )

internal fun ProgramGuideItem.Timeline.getBounds(dimensions: ProgramGuidePxDimensions): Rect =
    Rect(
        left = startHour * dimensions.timelineHourWidth,
        top = 0f,
        right = endHour * dimensions.timelineHourWidth,
        bottom = dimensions.timelineHeight.toFloat(),
    )
