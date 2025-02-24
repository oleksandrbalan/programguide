import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.jetbrains.cocoapods)
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.android.library)
	alias(libs.plugins.compose.compiler)
	id("convention.jvm.toolchain")
}

kotlin {
	androidTarget()

	jvm()

	iosX64()
	iosArm64()
	iosSimulatorArm64()

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		browser()
		binaries.library()
	}

	applyDefaultHierarchyTemplate()

	cocoapods {
		version = "1.0.0"
		summary = "Demo Compose Multiplatform module"
		homepage = "---"
		ios.deploymentTarget = "14.1"
		podfile = project.file("../iosdemo/Podfile")
		framework {
			baseName = "demo"
			isStatic = true
		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(":programguide"))

				implementation(compose.material3)
			}
		}

		all {
			languageSettings.optIn("androidx.compose.ui.text.ExperimentalTextApi")
			languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
		}
	}
}

android {
	namespace = "eu.wewox.programguide.demo"

	compileSdk = libs.versions.sdk.compile.get().toInt()
}
