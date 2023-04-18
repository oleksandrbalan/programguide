package eu.wewox.programguide

import androidx.compose.runtime.Composable
import eu.wewox.minabox.MinaBoxScope
import eu.wewox.programguide.ProgramGuideItem.Channel
import eu.wewox.programguide.ProgramGuideItem.CurrentTime
import eu.wewox.programguide.ProgramGuideItem.Program
import eu.wewox.programguide.ProgramGuideItem.Timeline
import eu.wewox.programguide.ProgramGuideItem.TopCorner

/**
 * Receiver scope which is used by [ProgramGuide].
 *
 * Note: Multiple calls of the same method will override the previous call(s).
 */
public interface ProgramGuideScope {

    /**
     * The start hour of the whole program guide. Default is 0:00.
     */
    public var guideStartHour: Float

    /**
     * Adds a [count] of programs.
     *
     * @param count The items count.
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun programs(
        count: Int,
        layoutInfo: (index: Int) -> Program,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    /**
     * Adds a vertical current time line.
     *
     * @param layoutInfo The lambda to provide layout information of the item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by an item.
     */
    public fun currentTime(
        layoutInfo: () -> CurrentTime,
        key: (() -> Any)? = null,
        contentType: () -> Any? = { null },
        itemContent: @Composable () -> Unit
    )

    /**
     * Adds a [count] of channels.
     *
     * @param count The items count.
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun channels(
        count: Int,
        layoutInfo: (index: Int) -> Channel,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    /**
     * Adds a [count] of timeline items.
     *
     * @param count The items count.
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun timeline(
        count: Int,
        layoutInfo: (index: Int) -> Timeline,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    /**
     * Adds a top corner.
     *
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by an item.
     */
    public fun topCorner(
        key: (() -> Any)? = null,
        contentType: () -> Any? = { null },
        itemContent: @Composable () -> Unit
    )

    /**
     * Adds given [items] as programs.
     *
     * @param items The items to add to the [ProgramGuide].
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun <T> programs(
        items: List<T>,
        layoutInfo: (item: T) -> Program,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ): Unit = programs(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )

    /**
     * Adds given [items] as channels.
     *
     * @param items The items to add to the [ProgramGuide].
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun <T> channels(
        items: List<T>,
        layoutInfo: (item: T) -> Channel,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ): Unit = channels(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )

    /**
     * Adds given [items] as timeline items.
     *
     * @param items The items to add to the [ProgramGuide].
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    public fun <T> timeline(
        items: List<T>,
        layoutInfo: (item: T) -> Timeline,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ): Unit = timeline(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )
}

/**
 * Implementation of the [MinaBoxScope] with [ProgramGuideItemsContent] properties.
 */
internal class ProgramGuideScopeImpl : ProgramGuideScope {

    override var guideStartHour: Float = 0f

    /**
     * Registered programs.
     */
    var programsContent: ProgramGuideItemContent? = null
        private set

    /**
     * Registered current time vertical line.
     */
    var currentTimeContent: ProgramGuideItemContent? = null
        private set

    /**
     * Registered channels.
     */
    var channelsContent: ProgramGuideItemContent? = null
        private set

    /**
     * Registered timeline items.
     */
    var timelinesContent: ProgramGuideItemContent? = null
        private set

    /**
     * Registered top corner.
     */
    var topCornerContent: ProgramGuideItemContent? = null
        private set

    override fun programs(
        count: Int,
        layoutInfo: (index: Int) -> Program,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        programsContent = ProgramGuideItemContent(count, layoutInfo, key, contentType, itemContent)
    }

    override fun currentTime(
        layoutInfo: () -> CurrentTime,
        key: (() -> Any)?,
        contentType: () -> Any?,
        itemContent: @Composable () -> Unit
    ) {
        currentTimeContent = singleItemContent(layoutInfo, key, contentType, itemContent)
    }

    override fun channels(
        count: Int,
        layoutInfo: (index: Int) -> Channel,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        channelsContent = ProgramGuideItemContent(count, layoutInfo, key, contentType, itemContent)
    }

    override fun timeline(
        count: Int,
        layoutInfo: (index: Int) -> Timeline,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        timelinesContent = ProgramGuideItemContent(count, layoutInfo, key, contentType, itemContent)
    }

    override fun topCorner(
        key: (() -> Any)?,
        contentType: () -> Any?,
        itemContent: @Composable () -> Unit
    ) {
        topCornerContent = singleItemContent({ TopCorner() }, key, contentType, itemContent)
    }

    private fun singleItemContent(
        layoutInfo: () -> ProgramGuideItem,
        key: (() -> Any)?,
        contentType: () -> Any?,
        itemContent: @Composable () -> Unit
    ): ProgramGuideItemContent =
        ProgramGuideItemContent(
            count = 1,
            layoutInfo = { layoutInfo() },
            key = key?.let { { key.invoke() } },
            contentType = { contentType() },
            itemContent = { itemContent() }
        )
}

internal class ProgramGuideItemContent(
    val count: Int,
    val layoutInfo: (index: Int) -> ProgramGuideItem,
    val key: ((index: Int) -> Any)?,
    val contentType: (index: Int) -> Any?,
    val itemContent: @Composable (index: Int) -> Unit
)
