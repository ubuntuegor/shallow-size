package to.bnt.shallow_size.compiler_plugin

import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.config.CompilerConfiguration

class MyComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)
        messageCollector?.report(CompilerMessageSeverity.STRONG_WARNING, "a")
    }
}
