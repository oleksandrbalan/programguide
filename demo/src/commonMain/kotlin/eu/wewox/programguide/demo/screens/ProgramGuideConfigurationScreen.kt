package eu.wewox.programguide.demo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import eu.wewox.programguide.demo.ui.theme.SpacingMedium

/**
 * Example of different configuration options.
 */
@Composable
fun ProgramGuideConfigurationScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = Example.ProgramGuideConfiguration.label,
                onBackClick = onBackClick,
            )
        }
    ) { padding ->
        val channels = 20
        val timeline = 8..22
        val programs = remember { createPrograms(channels, timeline) }

        Column(Modifier.padding(padding)) {
            var settings by remember { mutableStateOf(Settings()) }

            Settings(
                settings = settings,
                onChange = { settings = it },
            )

            ProgramGuide(
                channels = channels,
                timeline = timeline,
                programs = programs,
                settings = settings,
            )
        }
    }
}

@Composable
private fun ProgramGuide(
    channels: Int,
    timeline: IntRange,
    programs: List<Program>,
    settings: Settings,
    modifier: Modifier = Modifier
) {
    ProgramGuide(
        dimensions = dimensions(settings.showChannels, settings.showTimeline),
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

        if (settings.showChannels) {
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

        if (settings.showTimeline) {
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

        if (settings.showCurrentTime) {
            currentTime(
                layoutInfo = { ProgramGuideItem.CurrentTime(12.5f) },
                itemContent = { CurrentTimeLine() },
            )
        }

        if (settings.showCorner) {
            topCorner(
                itemContent = { TopCornerCell() },
            )
        }
    }
}

@Composable
private fun Settings(
    settings: Settings,
    onChange: (Settings) -> Unit,
) {
    @Composable
    fun SettingsRow(
        text: String,
        value: Boolean,
        onChange: (Boolean) -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingMedium),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = value,
                onCheckedChange = onChange
            )
        }
    }

    SettingsRow(
        text = "Show channels",
        value = settings.showChannels,
        onChange = { onChange(settings.copy(showChannels = it)) }
    )

    SettingsRow(
        text = "Show timeline",
        value = settings.showTimeline,
        onChange = { onChange(settings.copy(showTimeline = it)) }
    )

    SettingsRow(
        text = "Show current time",
        value = settings.showCurrentTime,
        onChange = { onChange(settings.copy(showCurrentTime = it)) }
    )

    SettingsRow(
        text = "Show corner",
        value = settings.showCorner,
        onChange = { onChange(settings.copy(showCorner = it)) }
    )
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

private data class Settings(
    val showChannels: Boolean = true,
    val showTimeline: Boolean = true,
    val showCurrentTime: Boolean = true,
    val showCorner: Boolean = true,
)
