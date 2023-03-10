@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.ProgramGuideState
import eu.wewox.programguide.demo.ui.theme.ProgramGuideTheme
import eu.wewox.programguide.rememberProgramGuideState
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Locale

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
                    Column {
                        val scope = rememberCoroutineScope()
                        val state = rememberProgramGuideState {
                            val index = Calendar.getInstance(Locale.getDefault()).get(HOUR_OF_DAY)
                            val x = getTimelinePosition(index, Alignment.CenterHorizontally)
                            Offset(x, 0f)
                        }
                        var programs by remember { mutableStateOf(createPrograms()) }

                        Row {
                            Button(onClick = { programs = createPrograms() }) {
                                Text(text = "Shuffle")
                            }
                            Button(onClick = {
                                scope.launch {
                                    state.snapToProgram(index = 1000)
                                }
                            }) {
                                Text(text = "Focus")
                            }
                        }

                        Guide(
                            state,
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
    val state = rememberLazyListState()
    LazyColumn(modifier, state) {
        items(
            items = programs,
            key = { it.title },
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
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

private fun createPrograms(): List<Program> {
    var channel = 0
    var hour = HOURS.random()
    return buildList {
        while (channel < 300) {
            while (hour < 24) {
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
