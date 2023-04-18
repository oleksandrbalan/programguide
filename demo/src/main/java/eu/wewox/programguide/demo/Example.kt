package eu.wewox.programguide.demo

/**
 * Enumeration of available demo examples.
 *
 * @param label Example name.
 * @param description Brief description.
 */
enum class Example(
    val label: String,
    val description: String,
) {
    SimpleProgramGuide(
        "Simple Program Guide",
        "Basic program guide usage"
    ),
    ConfigurableProgramGuide(
        "Configurable Program Guide",
        "Example of different configuration options"
    ),
}
