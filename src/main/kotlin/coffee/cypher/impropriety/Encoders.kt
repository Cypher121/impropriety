@file:OptIn(ExperimentalSerializationApi::class)

package coffee.cypher.impropriety

import coffee.cypher.impropriety.configuration.WriterConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal sealed class ImpropertiesFileEncoder(
    private val writerConfig: WriterConfig,
    private val destination: Appendable,
    override val serializersModule: SerializersModule
) : Encoder {
    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
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

    private fun createSubEncoder(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.CLASS -> TODO()
            StructureKind.LIST, is PolymorphicKind -> TODO()
            StructureKind.MAP -> TODO()
            else -> throw IllegalArgumentException()
        }
    }

    override fun encodeBoolean(value: Boolean) {
        unsupportedValue(value)
    }

    override fun encodeByte(value: Byte) {
        unsupportedValue(value)
    }

    override fun encodeChar(value: Char) {
        unsupportedValue(value)
    }

    override fun encodeDouble(value: Double) {
        unsupportedValue(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        unsupportedValue(enumDescriptor.getElementName(index))
    }

    override fun encodeFloat(value: Float) {
        unsupportedValue(value)
    }

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
        unsupportedValue(value)
    }

    override fun encodeLong(value: Long) {
        unsupportedValue(value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        unsupportedValue("null")
    }

    override fun encodeShort(value: Short) {
        unsupportedValue(value)
    }

    override fun encodeString(value: String) {
        unsupportedValue(value)
    }

    private fun unsupportedValue(value: Any): Nothing {
        throw ImpropertiesSerializationException(
            "Cannot encode top-level value: `$value`"
        )
    }
}

internal class BaseImpropertiesEncoder(
    protected val writerConfig: WriterConfig,
    protected val indentLevel: Int,
    protected val destination: Appendable,
    override val serializersModule: SerializersModule,
    private val onDone: (BaseImpropertiesEncoder) -> Unit
) : CompositeEncoder {
    protected fun appendIndent() = destination.apply {
        repeat(indentLevel) {
            destination.append(writerConfig.indentString)
        }
    }

    protected fun appendLiteral(literal: String) = destination.apply {
        val escaped =
            literal.replace("([#\\-\\s\t\r\n\\\\=:])".toRegex(), "\\\$1")
                .replace(
                    "\n",
                    "\n${writerConfig.indentString.repeat(indentLevel + 1)}"
                )

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

    override fun encodeElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Boolean {
        if (super.encodeElement(descriptor, index)) {
            beforeElement(descriptor.getElementName(index))

            return true
        }

        return false
    }

    abstract fun encodeElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Any
    )

    override fun encodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Byte
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Char
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Double
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Float
    ) {
        TODO("Not yet implemented")
    }

    @ExperimentalSerializationApi
    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Encoder = this

    override fun encodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Long
    ) {
        TODO("Not yet implemented")
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        TODO("Not yet implemented")
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Short
    ) {
        TODO("Not yet implemented")
    }

    override fun encodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: String
    ) {
        TODO("Not yet implemented")
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
) : BaseImpropertiesEncoder(
    writerConfig,
    indentLevel,
    destination,
    serializersModule,
    onDone
) {
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
) : BaseImpropertiesEncoder(
    writerConfig,
    indentLevel,
    destination,
    serializersModule,
    onDone
) {
    override fun beforeElement(name: String) {
        super.beforeElement(name)
        destination.append('-')
    }

    override fun beforeValue() {
        destination.append(' ')
    }
}

public class ImpropertiesSerializationException : SerializationException {
    internal constructor()

    internal constructor(message: String?) : super(message)

    internal constructor(message: String?, cause: Throwable?) : super(message, cause)

    internal constructor(cause: Throwable?) : super(cause)
}