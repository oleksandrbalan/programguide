package eu.wewox.minabox

import androidx.compose.ui.geometry.Offset

data class MinaBoxItem(
    val offset: Offset,
    val width: Value,
    val height: Value,
    val lockHorizontally: Boolean = false,
    val lockVertically: Boolean = false,
) {
    sealed interface Value {
        @JvmInline
        value class Absolute(val value: Float) : Value

        @JvmInline
        value class Relative(val fraction: Float) : Value
    }
}
