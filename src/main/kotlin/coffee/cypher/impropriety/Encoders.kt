@file:OptIn(ExperimentalSerializationApi::class)

package coffee.cypher.impropriety

import coffee.cypher.impropriety.configuration.WriterConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.IllegalArgumentException

internal class ImpropertiesFileEncoder(
    private val writerConfig: WriterConfig,
    private val destination: Appendable,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    override fun encodeValue(value: Any) {
        throw IllegalArgumentException("Improperties cannot encode top-level values like $value")
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        //throw IllegalArgumentException("Improperties cannot encode top-level lists")
        return ImpropertiesListEncoder(
            writerConfig = writerConfig,
            indentLevel = 0,
            destination = destination,
            serializersModule = serializersModule
        ) {}
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return ImpropertiesObjectEncoder(
            writerConfig = writerConfig,
            indentLevel = 0,
            destination = destination,
            serializersModule = serializersModule
        ) {}
    }
}

internal sealed class BaseImpropertiesEncoder(
    protected val writerConfig: WriterConfig,
    protected val indentLevel: Int,
    protected val destination: Appendable,
    override val serializersModule: SerializersModule,
    private val onDone: (BaseImpropertiesEncoder) -> Unit
) : AbstractEncoder() {
    protected fun appendIndent() = destination.apply {
        repeat(indentLevel) {
            destination.append(writerConfig.indentString)
        }
    }

    protected fun appendLiteral(literal: String) = destination.apply {
        val escaped = literal.replace("([#\\-\\s\t\r\n\\\\=:])".toRegex(), "\\\$1")
            .replace("\n", "\n${writerConfig.indentString.repeat(indentLevel + 1)}")

        append(escaped)
    }

    protected fun appendCompositeStarter() = destination.apply {
        appendLine(" ->")
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        appendCompositeStarter()

        return ImpropertiesListEncoder(
            writerConfig,
            indentLevel + 1,
            destination,
            serializersModule
        ) {
            appendIndent()
            appendCompositeCloser()
        }
    }

    protected fun appendCompositeCloser() = destination.appendLine("--")

    protected open fun beforeElement(name: String) {
        appendIndent()
    }

    protected open fun beforeValue() {}

    override fun encodeValue(value: Any) {
        beforeValue()
        appendLiteral(value.toString())
        destination.appendLine()
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (super.encodeElement(descriptor, index)) {
            beforeElement(descriptor.getElementName(index))

            return true
        }

        return false
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        onDone(this)
    }
}

internal class ImpropertiesObjectEncoder(
    writerConfig: WriterConfig,
    indentLevel: Int,
    destination: Appendable,
    serializersModule: SerializersModule,
    onDone: (BaseImpropertiesEncoder) -> Unit
) : BaseImpropertiesEncoder(writerConfig, indentLevel, destination, serializersModule, onDone) {
    override fun beforeElement(name: String) {
        super.beforeElement(name)
        appendLiteral(name)
    }

    override fun beforeValue() {
        destination.append(writerConfig.keyValueJoiner)
    }
}

internal class ImpropertiesListEncoder(
    writerConfig: WriterConfig,
    indentLevel: Int,
    destination: Appendable,
    serializersModule: SerializersModule,
    onDone: (BaseImpropertiesEncoder) -> Unit
) : BaseImpropertiesEncoder(writerConfig, indentLevel, destination, serializersModule, onDone) {
    override fun beforeElement(name: String) {
        super.beforeElement(name)
        destination.append('-')
    }

    override fun beforeValue() {
        destination.append(' ')
    }
}