# Shallow Copy: Kotlin compiler plugin

Creates a `shallowCopy(): Int` method for every data class in your code, which returns the precalculated size of its
primitive properties in bytes.

## Usage

_Compiler Plugin APIs are **experimental**! This plugin was tested with Kotlin **1.6.0**, **1.6.10**._

#### CLI compiler

1. Get the compiler-plugin jar by either downloading or compiling the code yourself
2. Use the plugin
    ```shell
    kotlinc main.kt -include-runtime -d main.jar -Xplugin=<path to plugin jar>
    ```
   
#### Gradle

1. Get the compiler-plugin-embeddable jar by either downloading or compiling the code yourself
2. Place the jar somewhere within your project
3. Use the plugin
    ```kotlin
   tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xplugin=${project.projectDir}/<path to plugin jar>"
        }
    }
   ```

## Example

```kotlin
data class Position(
    val x: Double,
    val y: Double
)

val pos = Position(3.0, -10.0)
println(pos.shallowSize()) // 16
```