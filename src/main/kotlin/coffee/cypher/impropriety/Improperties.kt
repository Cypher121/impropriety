package coffee.cypher.impropriety

import coffee.cypher.impropriety.configuration.ImpropertiesConfig
import kotlinx.serialization.*

public open class Improperties @JvmOverloads constructor(
    public val config: ImpropertiesConfig = ImpropertiesConfig.DEFAULT
) {

    public companion object Default : Improperties()

    public constructor(config: ImpropertiesConfig.Builder) :
            this(config.build())

    public constructor(config: ImpropertiesConfig.Builder.() -> Unit) :
            this(ImpropertiesConfig.Builder().apply(config))

    //generic

    /**
     * Writes the given [value] to the [destination] as an improperties file.
     */
    public fun <T : Any, R : Appendable> encodeToAppendable(
        serializer: SerializationStrategy<T>,
        value: T,
        destination: R
    ): R {
        serializer.serialize(
            ImpropertiesFileEncoder(
                config.writer,
                destination,
                config.module
            ),
            value
        )

        return destination
    }

    /**
     * Writes the given [value] to a string as an improperties file.
     */
    public fun <T : Any> encodeToString(
        serializer: SerializationStrategy<T>,
        value: T
    ): String = StringBuilder().apply {
        encodeToAppendable(serializer, value, this)
    }.toString()

    //map

    public fun encodeMapToAppendable(
        value: Map<String, Any>,
        destination: Appendable
    ): Appendable = encodeToAppendable(TaggedStructure.of(value), destination)

    public fun encodeMapToString(value: Map<String, Any>): String =
        StringBuilder().apply {
            encodeMapToAppendable(value, this)
        }.toString()
}

public inline fun <reified T : Any> Improperties.encodeToAppendable(
    value: T,
    destination: Appendable
): Appendable = encodeToAppendable(config.module.serializer(), value, destination)

public inline fun <reified T : Any> Improperties.encodeToString(
    value: T
): String = StringBuilder().apply {
    encodeToAppendable(value, this)
}.toString()


