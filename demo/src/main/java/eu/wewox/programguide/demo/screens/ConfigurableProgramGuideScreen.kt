@file:OptIn(ExperimentalMaterial3Api::class)

package eu.wewox.programguide.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideDefaults
import eu.wewox.programguide.ProgramGuideDimensions
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.demo.Example
import eu.wewox.programguide.demo.data.Program
import eu.wewox.programguide.demo.data.createPrograms
import eu.wewox.programguide.demo.ui.components.ChannelCell
import eu.wewox.programguide.demo.ui.components.CurrentTimeLine
import eu.wewox.programguide.demo.ui.components.ProgramCell
import eu.wewox.programguide.demo.ui.components.TimelineItemCell
import eu.wewox.programguide.demo.ui.components.TopBar
import eu.wewox.programguide.demo.ui.components.TopCornerCell

/**
 * Example of different configuration options.
 */
@Composable
fun ConfigurableProgramGuideScreen() {
    Scaffold(
        topBar = { TopBar(Example.SimpleProgramGuide.label) }
    ) { padding ->
        val channels = 20
        val timeline = 8..22
        val programs = remember { createPrograms(channels, timeline) }

        Column(Modifier.padding(padding)) {
            // TODO: Add switches

            ProgramGuide(
                channels = channels,
                timeline = timeline,
                programs = programs,
                showChannels = true,
                showTimeline = true,
                showCurrentTime = true,
                showCorner = true,
            )
        }
    }
}

@Composable
private fun ProgramGuide(
    channels: Int,
    timeline: IntRange,
    programs: List<Program>,
    showChannels: Boolean,
    showTimeline: Boolean,
    showCurrentTime: Boolean,
    showCorner: Boolean,
    modifier: Modifier = Modifier
) {
    ProgramGuide(
        dimensions = dimensions(showChannels, showTimeline),
        modifier = modifier.fillMaxWidth()
    ) {
        guideStartHour = timeline.first.toFloat()

        programs(
            items = programs,
            layoutInfo = {
                ProgramGuideItem.Program(
                    channelIndex = it.channel,
                    startHour = it.start,
                    endHour = it.end,
                )
            },
            itemContent = { ProgramCell(it) },
        )

        if (showChannels) {
            channels(
                count = channels,
                layoutInfo = {
                    ProgramGuideItem.Channel(
                        index = it
                    )
                },
                itemContent = { ChannelCell(it) },
            )
        }

        if (showTimeline) {
            timeline(
                count = timeline.count(),
                layoutInfo = {
                    val start = timeline.toList()[it].toFloat()
                    ProgramGuideItem.Timeline(
                        startHour = start,
                        endHour = start + 1f
                    )
                },
                itemContent = { TimelineItemCell(timeline.toList()[it].toFloat()) },
            )
        }

        if (showCurrentTime) {
            currentTime(
                layoutInfo = { ProgramGuideItem.CurrentTime(12.5f) },
                itemContent = { CurrentTimeLine() },
            )
        }

        if (showCorner) {
            topCorner(
                itemContent = { TopCornerCell() },
            )
        }
    }
}

private fun dimensions(
    showChannels: Boolean,
    showTimeline: Boolean,
): ProgramGuideDimensions =
    ProgramGuideDefaults.dimensions.let {
        it.copy(
            channelWidth = if (showChannels) it.channelWidth else 0.dp,
            timelineHeight = if (showTimeline) it.timelineHeight else 0.dp,
        )
    }
