@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("all")

package eu.wewox.programguide.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.wewox.minabox.MinaBox
import eu.wewox.minabox.MinaBoxItem
import eu.wewox.minabox.MinaBoxItem.Value.Absolute
import eu.wewox.minabox.MinaBoxItem.Value.MatchParent
import eu.wewox.minabox.rememberMinaBoxState
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.ProgramGuideState
import eu.wewox.programguide.demo.ui.theme.ProgramGuideTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgramGuideTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    List(PROGRAMS)
                    Column {
                        val scope = rememberCoroutineScope()
//                        val state = rememberProgramGuideState {
//                            val index = Calendar.getInstance(Locale.getDefault()).get(HOUR_OF_DAY)
//                            val x = getTimelinePosition(index, Alignment.CenterHorizontally)
//                            Offset(x, 0f)
//                        }
                        var programs by remember { mutableStateOf(createPrograms()) }

                        Row {
                            Button(onClick = {
                                programs = createPrograms(programs.last().channel - 2)
                            }) {
                                Text(text = "Shuffle")
                            }
//                            Button(onClick = {
//                                scope.launch {
//                                    state.snapToProgram(index = 1000)
//                                }
//                            }) {
//                                Text(text = "Focus")
//                            }
                        }

                        MinaBoxSample(
                            programs,
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun List(programs: List<Program>, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    LazyColumn(modifier, state) {
        items(
            count = programs.size,
        ) { index ->
            val it = programs[index]
            Surface(
                color = MaterialTheme.colorScheme.primary,
                onClick = { scope.launch { state.scrollToItem(index) } },
                modifier = Modifier.padding(1.dp)
            ) {
                Column(modifier = Modifier.padding(2.dp)) {
                    Text(it.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${it.start} - ${it.end}", fontSize = 12.sp)
                }
            }
        }

        items(
            count = programs.size,
        ) { index ->
            val it = programs[index]
            Surface(
                color = MaterialTheme.colorScheme.primary,
                onClick = { scope.launch { state.scrollToItem(index) } },
                modifier = Modifier.padding(1.dp)
            ) {
                Column(modifier = Modifier.padding(2.dp)) {
                    Text(it.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${it.start} - ${it.end}", fontSize = 12.sp)
                }
            }
        }

        items(
            count = programs.size,
        ) { index ->
            val it = programs[index]
            Surface(
                color = MaterialTheme.colorScheme.primary,
                onClick = { scope.launch { state.scrollToItem(index) } },
                modifier = Modifier.padding(1.dp)
            ) {
                Column(modifier = Modifier.padding(2.dp)) {
                    Text(it.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${it.start} - ${it.end}", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun Guide(
    state: ProgramGuideState,
    programs: List<Program>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    ProgramGuide(
        modifier = modifier,
        state = state,
    ) {
        programs(
            items = programs,
            key = { it.title },
            layoutInfo = Program::toLayoutInfo,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(1.dp),
                onClick = {
                    scope.launch {
                        state.animateToProgram(programs.indexOf(it))
                    }
                }
            ) {
                Column(modifier = Modifier.padding(2.dp)) {
                    Text(
                        it.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("${it.start} - ${it.end}", fontSize = 12.sp)
                }
            }
        }

        channels(
            count = 300,
            key = { it },
            layoutInfo = { ProgramGuideItem.Channel(it) }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color.White),
                onClick = {
                    scope.launch {
                        state.animateToChannel(it)
                    }
                }
            ) {
                Text(
                    text = "Ch #$it",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        timeline(
            count = 24,
            key = { it },
            layoutInfo = { ProgramGuideItem.Timeline(it.toFloat(), it.toFloat() + 1f) }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color.White),
            ) {
                Text(
                    text = "$it - ${it + 1}",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

@Composable
private fun MinaBoxSample(programs: List<Program>, modifier: Modifier = Modifier) {
    val state = rememberMinaBoxState { getOffset(programs.size, paddingStart = 200f) }
    val scope = rememberCoroutineScope()
    MinaBox(
        state = state,
        modifier = modifier.padding(0.dp)
    ) {
        items(
            items = programs,
            layoutInfo = {
                MinaBoxItem(
                    x = it.start * HOUR_WIDTH + CHANNEL_SIZE,
                    y = it.channel * CHANNEL_SIZE + TIMELINE_HEIGHT,
                    width = (it.end - it.start) * HOUR_WIDTH,
                    height = CHANNEL_SIZE,
                )
            },
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(1.dp),
                onClick = {
                    scope.launch {
                        state.animateTo(programs.indexOf(it), paddingStart = 200f, paddingTop = 40f)
                    }
                }
            ) {
                Column(modifier = Modifier.padding(2.dp)) {
                    Text(
                        it.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("${it.start} - ${it.end}", fontSize = 12.sp)
                }
            }
        }

        items(
            count = 1,
            layoutInfo = {
                MinaBoxItem(
                    x = 3000f,
                    y = 0f,
                    width = Absolute(10f),
                    height = MatchParent(1f),
                    lockVertically = true,
                )
            }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.error,
            ) {}
        }

        items(
            count = programs.last().channel + 1,
            layoutInfo = {
                MinaBoxItem(
                    x = 0f,
                    y = it * CHANNEL_SIZE + TIMELINE_HEIGHT,
                    width = CHANNEL_SIZE,
                    height = CHANNEL_SIZE,
                    lockHorizontally = true,
                )
            }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color.White),
            ) {
                Text(
                    text = "Ch #$it",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        items(
            count = programs.maxOf { it.end }.toInt() + 1,
            layoutInfo = {
                MinaBoxItem(
                    x = it * HOUR_WIDTH + CHANNEL_SIZE,
                    y = 0f,
                    width = HOUR_WIDTH,
                    height = TIMELINE_HEIGHT,
                    lockVertically = true,
                )
            }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color.White),
            ) {
                Text(
                    text = "$it - ${it + 1}",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        items(
            count = 1,
            layoutInfo = {
                MinaBoxItem(
                    x = 0f,
                    y = 0f,
                    width = CHANNEL_SIZE,
                    height = TIMELINE_HEIGHT,
                    lockHorizontally = true,
                    lockVertically = true,
                )
            }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
            ) {}
        }
    }
}

private data class Program(
    val channel: Int,
    val start: Float,
    val end: Float,
    val title: String,
)

private val PROGRAMS = listOf(
    Program(0, 00.00f, 05.00f, "Program #0"),
    Program(0, 05.00f, 06.00f, "Program #1"),
    Program(0, 06.00f, 06.50f, "Program #2"),
    Program(0, 06.50f, 16.50f, "Program #3"),
    Program(0, 16.50f, 20.75f, "Program #4"),
    Program(1, 03.00f, 07.00f, "Program #5"),
    Program(1, 07.00f, 08.75f, "Program #6"),
    Program(1, 08.75f, 20.75f, "Program #7"),
    Program(1, 20.75f, 22.00f, "Program #8"),
)

private val HOURS = listOf(0.5f, 1f, 1.25f, 1.5f, 2f, 2.25f, 2.5f)

private const val CHANNELS_COUNT = 30
private const val HOURS_COUNT = 24

private const val HOUR_WIDTH = 400f
private const val TIMELINE_HEIGHT = 60f
private const val CHANNEL_SIZE = 200f

private fun createPrograms(channels: Int = CHANNELS_COUNT): List<Program> {
    var channel = 0
    var hour = HOURS.random()
    return buildList {
        while (channel < channels) {
            while (hour < HOURS_COUNT) {
                val end = hour + HOURS.random()
                add(Program(channel, hour, end, "Program #$size"))
                hour = end
            }
            hour = HOURS.random()
            channel += 1
        }
    }
}

private fun Program.toLayoutInfo(): ProgramGuideItem.Program =
    ProgramGuideItem.Program(
        channelIndex = channel,
        startHour = start,
        endHour = end,
    )
