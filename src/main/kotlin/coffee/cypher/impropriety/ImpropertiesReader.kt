package coffee.cypher.impropriety

import coffee.cypher.impropriety.model.ImpNode
import coffee.cypher.impropriety.model.ImpObject
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import java.io.File
import java.io.FileNotFoundException

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
        val listener = AccumulatingErrorListener()

        val parser = stream
            .let(::ImpropertiesLexer)
            .also { it.replaceListener(listener) }
            .let(::CommonTokenStream)
            .let(::ImpropertiesParser)
            .also { it.replaceListener(listener) }


        this.map = parser.file().`val`.also { listener.validate() }
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

private fun Recognizer<*, *>.replaceListener(replacement: ANTLRErrorListener) {
    removeErrorListener(ConsoleErrorListener.INSTANCE)
    addErrorListener(replacement)
}

private class AccumulatingErrorListener : BaseErrorListener() {
    private val messages = mutableListOf<String>()

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        messages += "${msg.replace("\n", "\\n")} at position $line:$charPositionInLine"
    }

    fun validate() {
        if (messages.isNotEmpty()) {
            val errorMessage =
                """
                    |Failed to parse improperties input, ${messages.size} errors encountered:
                    |${messages.joinToString("\n") { it.prependIndent() }}
                """.trimMargin()

            throw IllegalArgumentException(errorMessage)
        }
    }
}