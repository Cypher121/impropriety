package coffee.cypher.impropriety

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TaggedStructure.Serializer::class)
internal sealed class TaggedStructure {
    companion object {
        fun of(value: Any): TaggedStructure = when (value) {
            is Map<*, *> -> TaggedMap(value.mapNotNull { (k, v) ->
                v?.let { k.toString() to of(it) }
            }.toMap())

            is List<*> -> TaggedList(value.filterNotNull().map { of(it) })

            else -> TaggedLiteral(value.toString())
        }
    }

    object Serializer : KSerializer<TaggedStructure> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor by lazy {
            buildSerialDescriptor("TaggedStructure", SerialKind.CONTEXTUAL) {

            }
        }

        override fun deserialize(decoder: Decoder): TaggedStructure {
            TODO()
        }

        override fun serialize(encoder: Encoder, value: TaggedStructure) {
            when (value) {
                is TaggedList -> TaggedList.Serializer.serialize(encoder, value)
                is TaggedLiteral -> TaggedLiteral.Serializer.serialize(encoder, value)
                is TaggedMap -> TaggedMap.Serializer.serialize(encoder, value)
            }
        }

    }

    @Serializable(with = TaggedMap.Serializer::class)
    data class TaggedMap(val map: Map<String, TaggedStructure>) : TaggedStructure() {
        object Serializer : KSerializer<TaggedMap> {
            private val backingSerializer = MapSerializer(String.serializer(), TaggedStructure.Serializer)

            override val descriptor = backingSerializer.descriptor

            override fun deserialize(decoder: Decoder): TaggedMap {
                return TaggedMap(backingSerializer.deserialize(decoder))
            }

            override fun serialize(encoder: Encoder, value: TaggedMap) {
                backingSerializer.serialize(encoder, value.map)
            }
        }
    }

    @Serializable(with = TaggedList.Serializer::class)
    data class TaggedList(val list: List<TaggedStructure>) : TaggedStructure() {
        object Serializer : KSerializer<TaggedList> {
            private val backingSerializer = ListSerializer(TaggedStructure.serializer())

            override val descriptor = backingSerializer.descriptor

            override fun deserialize(decoder: Decoder): TaggedList {
                return TaggedList(backingSerializer.deserialize(decoder))
            }

            override fun serialize(encoder: Encoder, value: TaggedList) {
                backingSerializer.serialize(encoder, value.list)
            }
        }
    }

    @Serializable(with = TaggedLiteral.Serializer::class)
    data class TaggedLiteral(val literal: String) : TaggedStructure() {
        object Serializer : KSerializer<TaggedLiteral> {
            private val backingSerializer = String.serializer()

            override val descriptor = backingSerializer.descriptor

            override fun deserialize(decoder: Decoder): TaggedLiteral {
                return TaggedLiteral(backingSerializer.deserialize(decoder))
            }

            override fun serialize(encoder: Encoder, value: TaggedLiteral) {
                backingSerializer.serialize(encoder, value.literal)
            }
        }
    }
}

public fun main() {
    val d: Map<String, Any> = mapOf(
        "abc" to listOf(1, 2, 3),
        "def" to "xyz"
    )

    Improperties.encodeMapToString(d).apply(::println)
}