import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import eu.wewox.programguide.demo.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ProgramGuide",
    ) {
        App()
    }
}
