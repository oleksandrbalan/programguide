package eu.wewox.programguide.demo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.wewox.programguide.demo.data.Program
import eu.wewox.programguide.demo.data.formatTime

/**
 * Single program cell in program guide.
 *
 * @param program The program data.
 * @param modifier The modifier instance for the root composable.
 * @param onClick Callback to be called when the surface is clicked.
 */
@Composable
fun ProgramCell(program: Program, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = program.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = formatTime(program.start, program.end),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

/**
 * Single channel cell in program guide.
 *
 * @param index The channel index.
 * @param modifier The modifier instance for the root composable.
 * @param onClick Callback to be called when the surface is clicked.
 */
@Composable
fun ChannelCell(index: Int, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "Ch #$index",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}

/**
 * Single timeline item cell in program guide.
 *
 * @param hour The timeline item hour.
 * @param modifier The modifier instance for the root composable.
 * @param onClick Callback to be called when the surface is clicked.
 */
@Composable
fun TimelineItemCell(hour: Float, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Text(
                text = formatTime(hour),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Current time vertical line.
 *
 * @param modifier The modifier instance for the root composable.
 */
@Composable
fun CurrentTimeLine(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier,
    ) {
        // Empty
    }
}

/**
 * Top corner cell item.
 *
 * @param modifier The modifier instance for the root composable.
 */
@Composable
fun TopCornerCell(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        // Empty
    }
}
