package to.bnt.shallowsize.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOriginImpl
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

class ShallowSizeIrTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoid() {
    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.isShallowSize) {
            messageCollector.report(
                CompilerMessageSeverity.LOGGING,
                "Visiting ${declaration.name} on ${declaration.parentAsClass.name}"
            )
            val parent = declaration.parentAsClass
            parent.calculateSize()?.let { size ->
                declaration.origin = ShallowSizeIrOrigin
                declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
                    +irReturn(irInt(size))
                }
            }
        }
        return super.visitFunction(declaration)
    }

    private val IrFunction.isShallowSize
        get() = parent is IrClass &&
                parentAsClass.isData &&
                name == ShallowSizeFunctionProperties.name &&
                valueParameters.isEmpty() &&
                returnType == pluginContext.irBuiltIns.intType

    private fun IrClass.calculateSize(): Int? {
        return primaryConstructor?.valueParameters?.let {
            it.fold(0) { acc, value ->
                acc + value.tryToGetSize(name)
            }
        }
    }

    private fun IrValueParameter.tryToGetSize(parentName: Name): Int {
        pluginContext.referenceClass(this.type.classFqName!!)
        return type.let {
            when (true) {
                it.isByte() -> 1
                it.isShort() -> 2
                it.isInt() -> 4
                it.isLong() -> 8
                it.isFloat() -> 4
                it.isDouble() -> 8
                it.isBoolean() -> 1
                it.isChar() -> 2 // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/-s-i-z-e_-b-y-t-e-s.html
                else -> {
                    messageCollector.report(
                        CompilerMessageSeverity.WARNING,
                        "Unknown size for member <${this.name}: ${this.type.classFqName}> in data class " +
                                "<$parentName>, shallow size will be inaccurate"
                    )
                    0
                }
            }
        }
    }

    companion object {
        object ShallowSizeIrOrigin : IrDeclarationOriginImpl("SHALLOW_SIZE_IR_ORIGIN")
    }
}
