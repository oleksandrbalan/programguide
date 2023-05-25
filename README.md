[![Maven Central](https://img.shields.io/maven-central/v/io.github.oleksandrbalan/programguide.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.oleksandrbalan/programguide)

<img align="right" width="128" src="https://github.com/oleksandrbalan/programguide/assets/20944869/40f7d757-ff5f-4b9a-a4d4-5666a46d09f9">

# Program Guide

Program Guide, aka EPG, library for Jetpack Compose.

Lazy layout to display program guide data on the two directional plane. It is build on the [MinaBox](https://github.com/oleksandrbalan/minabox) (which is build on `LazyLayout`) and provides methods to register item(s) and handles scrolling on the plane.

## Usage

### Get a dependency

**Step 1.** Add the MavenCentral repository to your build file.
Add it in your root `build.gradle` at the end of repositories:
```
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

Or in `settings.gradle`:
```
pluginManagement {
    repositories {
        ...
        mavenCentral()
    }
}
```

**Step 2.** Add the dependency.
Check latest version on the [releases page](https://github.com/oleksandrbalan/programguide/releases).
```
dependencies {
    implementation 'io.github.oleksandrbalan:programguide:$version'
}
```

### Use in Composable

The core element of the `ProgramGuide` layout is a `content` lambda, where program, channel and timeline items are registered in the similar manner as in `LazyColumn` or `LazyRow`. 

There are multiple types of items to register:
* Programs - program item cells. Each program must define channel index, and where it starts and ends.
* Channels - channel item cells, displayed to the left of the program guide. Each channel must define its index.
* Timeline - timeline cells, displayed on the top. Each timeline item must define where it starts and ends, so it is not locked to per-hour granularity.
* Current time - vertical line of the current time.
* Top corner - place for content in the top left corner, above channels and timeline.

**Note:** To be independent on date-time libraries, hours are defined as float numbers. For example: 9.5f represents 09:30 and 16.25f represents 16:15.

The size of the items are defined via `dimensions` parameter.

It is also possible to observe on the scroll state and change it programmatically using an instance of the `ProgramGuideState`.

```kotlin
ProgramGuide {
    programs(
        // Count of programs
        count = ...,
        // Necessary layout info of the single program cell
        layoutInfo = { ProgramGuideItem.Program(...) }
    ) {
        // Composable for single program cell
    }

    channels(
        // Count of channels
        count = ...,
        // Necessary layout info of the single channel cell
        layoutInfo = { ProgramGuideItem.Channel(...) }
    ) {
        // Composable for single channel cell
    }

    timeline(
        // Count of timeline blocks
        count = ...,
        // Necessary layout info of the single timeline cell
        layoutInfo = { ProgramGuideItem.Timeline(...) }
    ) {
        // Composable for single timeline cell
    }
}
```

See Demo application and [examples](demo/src/main/kotlin/eu/wewox/programguide/screens) for more usage examples.

## Examples

Simple EPG data.

https://github.com/oleksandrbalan/programguide/assets/20944869/20fefbce-80cc-4e43-87d9-e359edf64c35

Fully configurable layout.

https://github.com/oleksandrbalan/programguide/assets/20944869/9ce2ecc6-7b80-470b-9254-77dcd5e670c1

If you need further customization options, check [MinaBox](https://github.com/oleksandrbalan/minabox) library.
