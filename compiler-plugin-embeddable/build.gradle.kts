plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.6.10")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("syncSource")
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

// https://github.com/bnorm/kotlin-ir-plugin-template/blob/dc6f3536d9097eaa2511157ac11c979be11116c5/kotlin-ir-plugin-native/build.gradle.kts
tasks.register<Sync>("syncSource") {
    from(project(":compiler-plugin").fileTree("src"))
    into("src")
    filter {
        // Replace shadowed imports from plugin module
        when (it) {
            "import com.intellij.mock.MockProject" -> "import org.jetbrains.kotlin.com.intellij.mock.MockProject"
            else -> it
        }
    }
}
