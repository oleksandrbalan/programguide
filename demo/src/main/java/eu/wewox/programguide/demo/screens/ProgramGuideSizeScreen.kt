@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)

package eu.wewox.programguide.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideDefaults
import eu.wewox.programguide.ProgramGuideDimensions
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.demo.Example
import eu.wewox.programguide.demo.data.createPrograms
import eu.wewox.programguide.demo.ui.components.ChannelCell
import eu.wewox.programguide.demo.ui.components.ProgramCell
import eu.wewox.programguide.demo.ui.components.TimelineItemCell
import eu.wewox.programguide.demo.ui.components.TopBar
import eu.wewox.programguide.demo.ui.theme.SpacingMedium

/**
 * Example how to precalculate size for the layout.
 */
@Composable
fun ProgramGuideSizeScreen() {
    Scaffold(
        topBar = { TopBar(Example.ProgramGuideSize.label) }
    ) { padding ->
        val channels = 20
        val timeline = 8..22
        val programs = remember { createPrograms(channels, timeline) }

        var fontScaling by remember { mutableStateOf(1f) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Settings(fontScaling = fontScaling, onFontScalingChange = { fontScaling = it })

            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScaling)) {
                ProgramGuide(
                    dimensions = calculateProgramGuideDimensions(),
                    modifier = Modifier.fillMaxWidth()
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
    }
}

@Composable
private fun Settings(
    fontScaling: Float,
    onFontScalingChange: (Float) -> Unit,
) {
    Column {
        Text(
            text = """
                Program guide dimensions should be pre-calculated to support dynamic font sizes.
                Keep in mind, that calculations should be tightly coupled with a displayed UI. 
            """.trimIndent(),
            modifier = Modifier
                .padding(horizontal = SpacingMedium)
                .fillMaxWidth()
        )

        Slider(
            value = fontScaling,
            onValueChange = onFontScalingChange,
            valueRange = 1f..3f,
            modifier = Modifier
                .padding(SpacingMedium)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun calculateProgramGuideDimensions(): ProgramGuideDimensions =
    ProgramGuideDefaults.dimensions.copy(
        channelHeight = calculateProgramHeight(),
        channelWidth = calculateChannelWidth(),
        timelineHeight = calculateTimelineHeight(),
    )

@Composable
private fun calculateProgramHeight(): Dp {
    val textMeasurer = rememberTextMeasurer()

    val verticalPadding = 8.dp
    val title = textMeasurer.measure(
        text = "",
        maxLines = 1,
        style = LocalTextStyle.current
    )
    val label = textMeasurer.measure(
        text = "",
        maxLines = 1,
        style = MaterialTheme.typography.labelSmall
    )

    return with(LocalDensity.current) {
        title.size.height.toDp() + label.size.height.toDp() + verticalPadding
    }
}

@Composable
private fun calculateChannelWidth(): Dp {
    val textMeasurer = rememberTextMeasurer()

    val horizontalPadding = 8.dp
    val label = textMeasurer.measure(
        text = "Ch #MM",
        maxLines = 1,
        style = MaterialTheme.typography.labelSmall
    )

    return with(LocalDensity.current) { label.size.width.toDp() + horizontalPadding }
}

@Composable
private fun calculateTimelineHeight(): Dp {
    val textMeasurer = rememberTextMeasurer()

    val title = textMeasurer.measure(
        text = "",
        maxLines = 1,
        style = LocalTextStyle.current
    )

    return with(LocalDensity.current) { title.size.height.toDp() }
}
