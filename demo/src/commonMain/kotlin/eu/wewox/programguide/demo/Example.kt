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
    ProgramGuideSimple(
        "Simple Program Guide",
        "Basic program guide usage"
    ),
    ProgramGuideConfiguration(
        "Configurable Program Guide",
        "Example of different configuration options"
    ),
    ProgramGuideState(
        "Program Guide State",
        "Example how program guide state could be used"
    ),
    ProgramGuideSize(
        "Program Guide Size",
        "Example how to precalculate size for the layout"
    ),
}
