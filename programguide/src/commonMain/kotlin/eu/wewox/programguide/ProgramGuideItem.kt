package eu.wewox.programguide

import eu.wewox.minabox.MinaBoxItem

/**
 * The layout data for item inside program guide.
 *
 * Note: Hours used in data could contain decimal part to represent minutes. For example: 9.5f
 * represents 09:30 and 16.25f represents 16:15.
 */
public sealed interface ProgramGuideItem {

    /**
     * The layout data for program.
     *
     * @property channelIndex The index of the channel.
     * @property startHour The starting hour.
     * @property endHour The end hour.
     */
    public class Program(
        public val channelIndex: Int,
        public val startHour: Float,
        public val endHour: Float,
    ) : ProgramGuideItem

    /**
     * The layout data for current time vertical line.
     *
     * @property hour The current hour.
     */
    public class CurrentTime(
        public val hour: Float
    ) : ProgramGuideItem

    /**
     * The layout data for channel.
     *
     * @property index The index of the channel.
     */
    public class Channel(
        public val index: Int,
    ) : ProgramGuideItem

    /**
     * The layout data for timeline item.
     * Typically [startHour] is a whole number in the selected range and [endHour] is a value
     * greater by one than [startHour].
     *
     * @property startHour The starting hour.
     * @property endHour The end hour.
     */
    public class Timeline(
        public val startHour: Float,
        public val endHour: Float,
    ) : ProgramGuideItem

    /**
     * The layout data for top corner where horizontal timeline row crosses vertical channels
     * column. Could be used to add a label or to simply clip the space.
     */
    @Suppress("CanSealedSubClassBeObject") // Object is not needed
    public class TopCorner : ProgramGuideItem
}

/**
 * Maps program guide layout data to [MinaBoxItem]s.
 */
internal fun ProgramGuideItem.toMinaBoxItem(
    guideStartHour: Float,
    dimensions: ProgramGuidePxDimensions,
): MinaBoxItem = with(dimensions) {
    when (this@toMinaBoxItem) {
        is ProgramGuideItem.Program ->
            MinaBoxItem(
                x = (startHour - guideStartHour) * timelineHourWidth + channelWidth,
                y = channelIndex * channelHeight.toFloat() + timelineHeight,
                width = (endHour - startHour) * timelineHourWidth,
                height = channelHeight.toFloat(),
            )

        is ProgramGuideItem.CurrentTime ->
            MinaBoxItem(
                x = (hour - guideStartHour) * timelineHourWidth + channelWidth,
                y = 0f,
                width = MinaBoxItem.Value.Absolute(currentTimeWidth.toFloat()),
                height = MinaBoxItem.Value.MatchParent(1f),
                lockVertically = true,
            )

        is ProgramGuideItem.Channel ->
            MinaBoxItem(
                x = 0f,
                y = index * channelHeight.toFloat() + timelineHeight,
                width = channelWidth.toFloat(),
                height = channelHeight.toFloat(),
                lockHorizontally = true,
            )

        is ProgramGuideItem.Timeline ->
            MinaBoxItem(
                x = (startHour - guideStartHour) * timelineHourWidth + channelWidth,
                y = 0f,
                width = (endHour - startHour) * timelineHourWidth,
                height = timelineHeight.toFloat(),
                lockVertically = true,
            )

        is ProgramGuideItem.TopCorner ->
            MinaBoxItem(
                x = 0f,
                y = 0f,
                width = channelWidth.toFloat(),
                height = timelineHeight.toFloat(),
                lockHorizontally = true,
                lockVertically = true,
            )
    }
}
