plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xjvm-default=all"
    }
}
