package to.bnt.shallowsize.plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

class ShallowSizeSyntheticResolveExtension(private val messageCollector: MessageCollector) : SyntheticResolveExtension {
    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor) =
        if (thisDescriptor.isShallowSizeApplicable) listOf(ShallowSizeFunctionProperties.name)
        else emptyList()

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
        if (name == ShallowSizeFunctionProperties.name) {
            val methodDescriptor = SimpleFunctionDescriptorImpl.create(
                thisDescriptor,
                Annotations.EMPTY,
                ShallowSizeFunctionProperties.name,
                CallableMemberDescriptor.Kind.SYNTHESIZED,
                thisDescriptor.source
            ).initialize(
                null,
                thisDescriptor.thisAsReceiverParameter,
                emptyList(),
                emptyList(),
                thisDescriptor.builtIns.intType,
                Modality.OPEN,
                DescriptorVisibilities.PUBLIC
            )

            result += methodDescriptor
        }
    }

    companion object {
        val ClassDescriptor.isShallowSizeApplicable: Boolean
            get() = this.isData
    }
}
