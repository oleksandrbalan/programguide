package eu.wewox.programguide.demo

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import eu.wewox.programguide.demo.screens.ProgramGuideConfigurationScreen
import eu.wewox.programguide.demo.screens.ProgramGuideSimpleScreen
import eu.wewox.programguide.demo.screens.ProgramGuideSizeScreen
import eu.wewox.programguide.demo.screens.ProgramGuideStateScreen
import eu.wewox.programguide.demo.ui.theme.ProgramGuideDemoTheme

@Composable
fun App() {
    var example by rememberSaveable { mutableStateOf<Example?>(null) }
    App(
        example = example,
        onChangeExample = { example = it },
    )
}

@Composable
fun App(
    example: Example?,
    onChangeExample: (Example?) -> Unit,
) {
    ProgramGuideDemoTheme {
        val reset = { onChangeExample(null) }

        Crossfade(targetState = example) { selected ->
            when (selected) {
                null -> RootScreen(onExampleClick = onChangeExample)
                Example.ProgramGuideSimple -> ProgramGuideSimpleScreen(reset)
                Example.ProgramGuideConfiguration -> ProgramGuideConfigurationScreen(reset)
                Example.ProgramGuideState -> ProgramGuideStateScreen(reset)
                Example.ProgramGuideSize -> ProgramGuideSizeScreen(reset)
            }
        }
    }
}
