package eu.wewox.programguide

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import eu.wewox.minabox.MinaBoxPositionProvider

/**
 * Interface to provide offset of the registered program guide items.
 */
public interface ProgramGuidePositionProvider : MinaBoxPositionProvider {
    /**
     * Returns offset of the program item with local [index].
     *
     * @param index The global index of the item.
     * @param alignment The alignment to align item inside the [ProgramGuide].
     * @return An item offset.
     */
    public fun getProgramOffset(
        index: Int,
        alignment: Alignment = Alignment.Center
    ): Offset

    /**
     * Returns position on X axis of the current time line.
     *
     * @param alignment The alignment to align item inside the [ProgramGuide].
     * @return An item offset.
     */
    public fun getCurrentTimePosition(
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ): Float

    /**
     * Returns position on Y axis of the channel item with local [index].
     *
     * @param alignment The alignment to align item inside the [ProgramGuide].
     * @return An item offset.
     */
    public fun getChannelPosition(
        index: Int,
        alignment: Alignment.Vertical = Alignment.CenterVertically
    ): Float

    /**
     * Returns position on X axis of the timeline item with local [index].
     *
     * @param alignment The alignment to align item inside the [ProgramGuide].
     * @return An item offset.
     */
    public fun getTimelinePosition(
        index: Int,
        alignment: Alignment.Horizontal = Alignment.CenterHorizontally
    ): Float
}

/**
 * Implementation of the [ProgramGuidePositionProvider] with [MinaBoxPositionProvider].
 *
 * @property positionProvider The instance of the [MinaBoxPositionProvider].
 * @property dimensions The dimensions of the program guide in pixels.
 * @property indexMapper The local to global indices mapper.
 */
internal class ProgramGuidePositionProviderImpl(
    private val positionProvider: MinaBoxPositionProvider,
    private val dimensions: ProgramGuidePxDimensions,
    private val indexMapper: ProgramGuideIndexMapper,
) : ProgramGuidePositionProvider, MinaBoxPositionProvider by positionProvider {

    override fun getProgramOffset(index: Int, alignment: Alignment): Offset =
        getOffset(
            index = indexMapper.getProgramIndex(index),
            alignment = alignment,
            paddingStart = dimensions.channelWidth.toFloat(),
            paddingTop = dimensions.timelineHeight.toFloat(),
        )

    override fun getCurrentTimePosition(alignment: Alignment.Horizontal): Float =
        getOffset(
            index = indexMapper.getCurrentTimeIndex(),
            alignment = alignment.toAlignment(),
            paddingStart = dimensions.channelWidth.toFloat(),
        ).x

    override fun getChannelPosition(index: Int, alignment: Alignment.Vertical): Float =
        getOffset(
            index = indexMapper.getChannelIndex(index),
            alignment = alignment.toAlignment(),
            paddingTop = dimensions.timelineHeight.toFloat(),
        ).y

    override fun getTimelinePosition(index: Int, alignment: Alignment.Horizontal): Float =
        getOffset(
            index = indexMapper.getTimelinesIndex(index),
            alignment = alignment.toAlignment(),
            paddingStart = dimensions.channelWidth.toFloat(),
        ).x

    private fun Alignment.Vertical.toAlignment(): Alignment =
        when (this) {
            Alignment.Top -> Alignment.TopCenter
            Alignment.CenterVertically -> Alignment.Center
            Alignment.Bottom -> Alignment.BottomCenter
            else -> Alignment.Center
        }

    private fun Alignment.Horizontal.toAlignment(): Alignment =
        when (this) {
            Alignment.Start -> Alignment.CenterStart
            Alignment.CenterHorizontally -> Alignment.Center
            Alignment.End -> Alignment.CenterEnd
            else -> Alignment.Center
        }
}
