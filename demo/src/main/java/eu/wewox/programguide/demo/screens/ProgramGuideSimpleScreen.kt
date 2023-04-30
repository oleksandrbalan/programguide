@file:OptIn(ExperimentalMaterial3Api::class)

package eu.wewox.programguide.demo.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.demo.Example
import eu.wewox.programguide.demo.data.createPrograms
import eu.wewox.programguide.demo.ui.components.ChannelCell
import eu.wewox.programguide.demo.ui.components.ProgramCell
import eu.wewox.programguide.demo.ui.components.TimelineItemCell
import eu.wewox.programguide.demo.ui.components.TopBar

/**
 * Showcases the most simple usage of program guide.
 */
@Composable
fun ProgramGuideSimpleScreen() {
    Scaffold(
        topBar = { TopBar(Example.ProgramGuideSimple.label) }
    ) { padding ->
        val channels = 20
        val timeline = 8..22
        val programs = remember { createPrograms(channels, timeline) }

        ProgramGuide(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
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

            channels(
                count = channels,
                layoutInfo = {
                    ProgramGuideItem.Channel(
                        index = it
                    )
                },
                itemContent = { ChannelCell(it) },
            )

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
    }
}
