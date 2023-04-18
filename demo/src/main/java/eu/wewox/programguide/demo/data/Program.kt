package eu.wewox.programguide.demo.data

/**
 * The demo domain object for program.
 * For simplicity has only channel index and start & end hours, real programs could be much more
 * complicated.
 *
 * @property channel The channel index.
 * @property start The start hour.
 * @property end The end hour.
 * @property title The simple title of the program.
 */
data class Program(
    val channel: Int,
    val start: Float,
    val end: Float,
    val title: String,
)

/**
 * Formats time.
 */
fun formatTime(from: Float, to: Float? = null): String {
    fun Float.hour(): Int = toInt()
    fun Float.minutes(): Int = ((this % 1) * 60).toInt()

    val format = "%02d:%02d"
    val fromFormatted = String.format(format, from.hour(), from.minutes())
    return if (to != null) {
        val toFormatted = String.format(format, to.hour(), to.minutes())
        "$fromFormatted - $toFormatted"
    } else {
        fromFormatted
    }
}

/**
 * Creates a list of programs to view in program guide.
 */
fun createPrograms(
    channels: Int = CHANNELS_COUNT,
    timeline: IntRange = 0..HOURS_COUNT
): List<Program> {
    var channel = 0
    var hour = timeline.first + HOURS.random()
    return buildList {
        while (channel < channels) {
            while (true) {
                val end = hour + HOURS.random()
                if (end > timeline.last) {
                    break
                }

                add(Program(channel, hour, end, "Program #$size"))
                hour = end
            }
            hour = timeline.first + HOURS.random()
            channel += 1
        }
    }
}

private val HOURS = listOf(0.5f, 1f, 1.25f, 1.5f, 2f, 2.25f, 2.5f)

private const val CHANNELS_COUNT = 30
private const val HOURS_COUNT = 24
