package coffee.cypher.impropriety.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public object EncoderTest {
    public inline fun <reified T> encodeToString(value: T): String = encodeToString(serializer(), value)

    public fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        ImpropertiesObjectEncoder("obj", 0).encodeSerializableValue(serializer, value)
        return ""
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class ImpropertiesObjectEncoder(val mode: String, val level: Int) : AbstractEncoder() {
    override val serializersModule: SerializersModule = EmptySerializersModule

    private fun write(msg: String) = println("$mode (depth $level): $msg")

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        write("key $index: ${descriptor.getElementName(index)}")
        return super.encodeElement(descriptor, index)
    }

    override fun encodeValue(value: Any) {
        write("value $value")
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        write("begin structure")
        return ImpropertiesObjectEncoder("obj", level + 1)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        write("end structure")
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        write("begin collection of $collectionSize")
        return ImpropertiesObjectEncoder("list", level + 1)
    }
}

@Serializable
internal data class Test(val a: String, val b: List<Int>, val c: OtherTest)

@Serializable
internal data class OtherTest(val x: String, val y: String)

public fun main() {
    EncoderTest.encodeToString(Test("aVal", listOf(1, 2), OtherTest("xVal", "yVal")))
}