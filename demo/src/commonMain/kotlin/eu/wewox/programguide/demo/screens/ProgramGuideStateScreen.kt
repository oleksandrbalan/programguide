@file:Suppress("LongMethod")

package eu.wewox.programguide.demo.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.demo.Example
import eu.wewox.programguide.demo.data.createPrograms
import eu.wewox.programguide.demo.ui.components.ChannelCell
import eu.wewox.programguide.demo.ui.components.CurrentTimeLine
import eu.wewox.programguide.demo.ui.components.ProgramCell
import eu.wewox.programguide.demo.ui.components.TimelineItemCell
import eu.wewox.programguide.demo.ui.components.TopBar
import eu.wewox.programguide.demo.ui.theme.SpacingSmall
import eu.wewox.programguide.rememberProgramGuideState
import kotlinx.coroutines.launch

/**
 * Example how program guide state could be used.
 */
@Composable
fun ProgramGuideStateScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = Example.ProgramGuideState.label,
                onBackClick = onBackClick,
            )
        }
    ) { padding ->
        val channels = 20
        val timeline = 8..22
        val currentTime = 12.5f
        var programs by remember { mutableStateOf(createPrograms(channels, timeline)) }

        val scope = rememberCoroutineScope()
        val state = rememberProgramGuideState(
            initialOffset = {
                val x = getCurrentTimePosition()
                Offset(x, 0f)
            }
        )

        val buttonVisibilityThreshold = LocalDensity.current.run { 48.dp.toPx() }
        val showPrev by remember {
            derivedStateOf {
                val translate = state.minaBoxState.translate
                if (translate == null) {
                    false
                } else {
                    translate.x < buttonVisibilityThreshold
                }
            }
        }
        val showNext by remember {
            derivedStateOf {
                val translate = state.minaBoxState.translate
                if (translate == null) {
                    false
                } else {
                    translate.maxX - translate.x < buttonVisibilityThreshold
                }
            }
        }

        Box {
            ProgramGuide(
                state = state,
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
                    itemContent = {
                        ProgramCell(
                            program = it,
                            onClick = {
                                val index = programs.indexOf(it)
                                scope.launch { state.animateToProgram(index) }
                            }
                        )
                    },
                )

                channels(
                    count = channels,
                    layoutInfo = {
                        ProgramGuideItem.Channel(
                            index = it
                        )
                    },
                    itemContent = {
                        ChannelCell(
                            index = it,
                            onClick = {
                                scope.launch { state.animateToChannel(it) }
                            }
                        )
                    },
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
                    itemContent = {
                        TimelineItemCell(
                            hour = timeline.toList()[it].toFloat(),
                            onClick = {
                                scope.launch { state.animateToTimeline(it) }
                            }
                        )
                    },
                )

                currentTime(
                    layoutInfo = { ProgramGuideItem.CurrentTime(currentTime) },
                    itemContent = { CurrentTimeLine() },
                )
            }

            ControlButton(
                visible = showPrev,
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = { programs = createPrograms(channels, timeline) },
                modifier = Modifier.align(Alignment.CenterStart),
            )

            ControlButton(
                visible = showNext,
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { programs = createPrograms(channels, timeline) },
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

@Composable
private fun ControlButton(
    visible: Boolean,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.padding(SpacingSmall),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    }
}
