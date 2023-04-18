package eu.wewox.programguide

/**
 * Used to map local index to global program guide items indices.
 *
 * @property programsCount The count of programs in the layout.
 * @property currentTimeCount The count of current time lines (0 or 1).
 * @property channelsCount The count of channels in the layout.
 */
internal class ProgramGuideIndexMapper(
    private val programsCount: Int,
    private val currentTimeCount: Int,
    private val channelsCount: Int,
) {
    /**
     * Gets global index from program index.
     */
    fun getProgramIndex(index: Int): Int =
        index

    /**
     * Gets global index of the current time vertical line.
     */
    fun getCurrentTimeIndex(): Int =
        programsCount + 0

    /**
     * Gets global index from channel index.
     */
    fun getChannelIndex(index: Int): Int =
        programsCount + currentTimeCount + index

    /**
     * Gets global index from timeline item index.
     */
    fun getTimelinesIndex(index: Int): Int =
        programsCount + currentTimeCount + channelsCount + index
}
