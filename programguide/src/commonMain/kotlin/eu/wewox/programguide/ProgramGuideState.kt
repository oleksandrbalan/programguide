package eu.wewox.programguide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import eu.wewox.minabox.MinaBoxState

/**
 * Creates a [ProgramGuideState] that is remembered across compositions.
 *
 * @param initialOffset The lambda to provide initial offset on the plane.
 * @return Instance of the [ProgramGuideState].
 */
@Deprecated(
    message = "Use rememberSaveableProgramGuideState() which uses rememberSaveable API.",
    replaceWith = ReplaceWith(
        "rememberSaveableProgramGuideState(initialOffset)",
        "eu.wewox.programguide.rememberSaveableProgramGuideState"
    )
)
@Composable
public fun rememberProgramGuideState(
    initialOffset: ProgramGuidePositionProvider.() -> Offset = { Offset.Zero }
): ProgramGuideState {
    return remember { ProgramGuideState(initialOffset) }
}

/**
 * Creates a [ProgramGuideState] that is remembered across compositions and saved across activity or process recreation.
 *
 * @param initialOffset The lambda to provide initial offset on the plane.
 * @return Instance of the [ProgramGuideState].
 */
@Composable
public fun rememberSaveableProgramGuideState(
    initialOffset: ProgramGuidePositionProvider.() -> Offset = { Offset.Zero },
): ProgramGuideState {
    return rememberSaveable(
        saver = ProgramGuideState.Saver(),
        init = { ProgramGuideState(initialOffset) }
    )
}

/**
 * A state object that can be hoisted to control and observe scrolling.
 *
 * @property initialOffset The lambda to provide initial offset on the plane.
 */
@Stable
public class ProgramGuideState(
    internal val initialOffset: ProgramGuidePositionProvider.() -> Offset
) {
    internal lateinit var dimensions: ProgramGuidePxDimensions
    internal lateinit var indexMapper: ProgramGuideIndexMapper

    private val translateX: Float get() = minaBoxState.translate?.x ?: 0f

    private val translateY: Float get() = minaBoxState.translate?.y ?: 0f

    /**
     * The underlying [MinaBoxState] used to this state.
     */
    public val minaBoxState: MinaBoxState = MinaBoxState(
        initialOffset = {
            initialOffset(ProgramGuidePositionProviderImpl(this, dimensions, indexMapper))
        }
    )

    /**
     * The position provider used to get items offsets.
     */
    public val positionProvider: ProgramGuidePositionProvider
        get() = ProgramGuidePositionProviderImpl(
            positionProvider = minaBoxState.positionProvider,
            dimensions = dimensions,
            indexMapper = indexMapper
        )

    /**
     * Animates current offset to the program item with a given index.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun animateToProgram(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getProgramOffset(index, alignment)
        minaBoxState.animateTo(offset.x, offset.y)
    }

    /**
     * Snaps current offset to the program item with a given index.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun snapToProgram(index: Int, alignment: Alignment = Alignment.Center) {
        val offset = positionProvider.getProgramOffset(index, alignment)
        minaBoxState.snapTo(offset.x, offset.y)
    }

    /**
     * Animates current offset to the current time vertical line.
     *
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun animateToCurrentTime(
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ) {
        val position = positionProvider.getCurrentTimePosition(alignment)
        minaBoxState.animateTo(position, translateY)
    }

    /**
     * Snaps current offset to the current time vertical line.
     *
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun snapToCurrentTime(
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ) {
        val position = positionProvider.getCurrentTimePosition(alignment)
        minaBoxState.snapTo(position, translateY)
    }

    /**
     * Animates current offset to the channel item with a given index.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun animateToChannel(
        index: Int,
        alignment: Alignment.Vertical = Alignment.CenterVertically
    ) {
        val position = positionProvider.getChannelPosition(index, alignment)
        minaBoxState.animateTo(translateX, position)
    }

    /**
     * Snaps current offset to the channel item with a given index.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun snapToChannel(
        index: Int,
        alignment: Alignment.Vertical = Alignment.CenterVertically
    ) {
        val position = positionProvider.getChannelPosition(index, alignment)
        minaBoxState.snapTo(translateX, position)
    }

    /**
     * Animates current offset to the timeline item with a given index.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun animateToTimeline(
        index: Int,
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ) {
        val position = positionProvider.getTimelinePosition(index, alignment)
        minaBoxState.animateTo(position, translateY)
    }

    /**
     * Snaps current offset to the timeline item with a given index.
     * Position on the Y axis is not changed.
     *
     * @param index The program index.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     */
    public suspend fun snapToTimeline(
        index: Int,
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ) {
        val position = positionProvider.getTimelinePosition(index, alignment)
        minaBoxState.snapTo(position, translateY)
    }

    internal companion object {

        /**
         * Creates a [Saver] that can save and restore a [ProgramGuideState].
         *
         * @return A [Saver] instance for saving and restoring [ProgramGuideState].
         */
        fun Saver(): Saver<ProgramGuideState, *> = listSaver(
            save = {
                listOf(
                    it.minaBoxState.translate?.x ?: 0f,
                    it.minaBoxState.translate?.y ?: 0f,
                )
            },
            restore = {
                ProgramGuideState {
                    Offset(it[0], it[1])
                }
            }
        )
    }
}
