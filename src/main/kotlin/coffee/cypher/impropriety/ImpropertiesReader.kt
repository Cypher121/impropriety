package coffee.cypher.impropriety

import coffee.cypher.impropriety.model.*
import org.antlr.v4.runtime.*
import java.io.*
import java.lang.IllegalArgumentException

/**
 * Reads and parses an improperties file for conversion into other formats.
 *
 * Parsing is done at construction time.
 */
public class ImpropertiesReader private constructor(stream: CharStream) {
    public companion object {
        /**
         * Creates a reader from given text.
         *
         * @throws IllegalArgumentException if the input is malformed
         */
        @JvmStatic
        public fun fromText(text: String): ImpropertiesReader =
            text.let(ImpPreprocessor::process)
                .let(CharStreams::fromString)
                .let(::ImpropertiesReader)

        /**
         * Creates a reader from the given file.
         *
         * @throws IllegalArgumentException if the input is malformed
         *
         * @throws FileNotFoundException if the file cannot be read or doesn't exist
         */
        @JvmStatic
        public fun fromFile(file: File): ImpropertiesReader =
            fromText(file.readText())

        /**
         * Creates a reader from the file at the given path.
         *
         * @throws IllegalArgumentException if the input is malformed
         *
         * @throws FileNotFoundException if the file cannot be read or doesn't exist
         */
        @JvmStatic
        public fun fromFile(path: String): ImpropertiesReader =
            fromFile(File(path))
    }

    private val map: Map<String, Any>

    init {
        val parser = stream
            .let(::ImpropertiesLexer)
            .let(::CommonTokenStream)
            .let(::ImpropertiesParser)

        parser.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>,
                offendingSymbol: Any,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException
            ) {
                throw IllegalArgumentException("Malformed improperties", e)
            }
        })

        map = parser.file().`val`
    }

    /**
     * Converts the input to a standard read-only collection.
     * Lists are represented as [List]s, objects (including the root file) as [Map]s with [String] keys,
     * and terminal values as [String]s.
     */
    public fun toMap(): Map<String, Any> = map

    /**
     * Converts the input to an [ImpObject] for use with the [ImpNode] API.
     */
    public fun toObject(): ImpObject = ImpObject("<root>", map)
}