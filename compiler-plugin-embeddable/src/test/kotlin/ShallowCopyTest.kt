import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test
import to.bnt.shallowsize.plugin.ShallowSizeComponentRegistrar
import kotlin.test.assertEquals

class ShallowCopyTest {
    @Test
    fun `Example usage`() {
        val result = compileWithPlugin(
            sourceFile = SourceFile.kotlin(
                "Main.kt", """
                    data class Position(
                        val x: Double,
                        val y: Double
                    )
                    
                    fun main() {
                        val pos = Position(3.0, -10.0)
                        println("Result ShallowCopy size: " + pos.shallowSize())
                    }
                """.trimIndent()
            )
        )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    companion object {
        private fun compileWithPlugin(sourceFile: SourceFile) = KotlinCompilation().apply {
            sources = listOf(sourceFile)
            compilerPlugins = listOf(ShallowSizeComponentRegistrar())
            inheritClassPath = true
        }.compile()
    }
}
