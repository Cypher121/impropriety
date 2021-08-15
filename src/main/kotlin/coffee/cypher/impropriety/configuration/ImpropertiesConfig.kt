package coffee.cypher.impropriety.configuration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
public data class ImpropertiesConfig(
    public val writer: WriterConfig = WriterConfig.DEFAULT,
    public val reader: ReaderConfig = ReaderConfig.DEFAULT,
    public val module: SerializersModule = EmptySerializersModule
) {
    public companion object {
        @JvmField
        public val DEFAULT: ImpropertiesConfig = ImpropertiesConfig()
    }

    public class Builder {
        public var writer: WriterConfig = WriterConfig.DEFAULT
        public var reader: ReaderConfig = ReaderConfig.DEFAULT
        public var module: SerializersModule = EmptySerializersModule

        public fun writer(writer: WriterConfig): Builder = apply {
            this.writer = writer
        }

        public fun reader(reader: ReaderConfig): Builder = apply {
            this.reader = reader
        }

        public fun module(module: SerializersModule): Builder = apply {
            this.module = module
        }

        public fun writer(writer: WriterConfig.Builder): Builder = writer(writer.build())
        public fun reader(reader: ReaderConfig.Builder): Builder = reader(reader.build())

        public inline fun writer(writerConfig: WriterConfig.Builder.() -> Unit): Builder =
            writer(WriterConfig.Builder().apply(writerConfig))

        public inline fun reader(readerConfig: ReaderConfig.Builder.() -> Unit): Builder =
            reader(ReaderConfig.Builder().apply(readerConfig))

        public fun build(): ImpropertiesConfig = ImpropertiesConfig(writer, reader)
    }
}