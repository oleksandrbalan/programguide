package eu.wewox.minabox

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import eu.wewox.minabox.MinaBoxItem.Value.Absolute

interface MinaBoxPositionProvider {
    fun getOffset(index: Int, alignment: Alignment): Offset
}

internal class MinaBoxPositionProviderImpl(
    private val itemProvider: MinaBoxItemProvider,
    private val alignmentPadding: MinaBoxAlignmentPadding,
    private val size: Size,
) : MinaBoxPositionProvider {

    override fun getOffset(index: Int, alignment: Alignment): Offset =
        getOffset(index, alignment, 0f, 0f)

    fun getOffset(index: Int, alignment: Alignment, currentX: Float, currentY: Float): Offset {
        val info = itemProvider.cache[index] ?: return Offset.Zero
        val offset = alignment.align(
            IntSize(
                if (info.width is Absolute) info.width.value.toInt() else 0,
                if (info.height is Absolute) info.height.value.toInt() else 0,
            ),
            IntSize(
                (size.width - alignmentPadding.start - alignmentPadding.end).toInt(),
                (size.height - alignmentPadding.top - alignmentPadding.bottom).toInt(),
            ),
            LayoutDirection.Ltr // TODO: Add support for RTL
        )
        return Offset(
            if (info.lockHorizontally) currentX else (info.offset.x - offset.x - alignmentPadding.start),
            if (info.lockVertically) currentY else (info.offset.y - offset.y - alignmentPadding.top),
        )
    }
}
