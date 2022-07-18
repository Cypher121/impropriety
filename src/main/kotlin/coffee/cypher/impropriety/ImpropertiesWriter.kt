package coffee.cypher.impropriety

import java.io.*

private const val DEFAULT_INDENT = 2
private const val DEFAULT_JOINER = " = "
private const val DEFAULT_OUTER_SPACING = 0
private const val DEFAULT_INNER_SPACING = 0

private const val COMPOSITE_STARTER = "->"
private const val COMPOSITE_CLOSER = "--"

/**
 * A configurable writer for improperties files.
 *
 * @param indent configures indent for nested elements inside lists or objects.
 *
 * @param topLevelSpacing configures the number of blank lines between elements at the top level of the file.
 *
 * @param innerSpacing configures the number of blank lines between nested elements inside lists or objects.
 *
 * @param keyValueJoiner configures the token used for key-value pairs.
 */
public class ImpropertiesWriter(
    indent: Int = DEFAULT_INDENT,
    topLevelSpacing: Int = DEFAULT_OUTER_SPACING,
    innerSpacing: Int = DEFAULT_INNER_SPACING,
    private val keyValueJoiner: String = DEFAULT_JOINER
) {
    private val indentString: String
    private val topLevelSpacer: String
    private val innerSpacer: String

    init {
        require(keyValueJoiner.matches("^\\s*[=:]\\s*$".toRegex())) {
            "Invalid joiner $keyValueJoiner, must be COLON (:) or EQUALS (=) with any whitespace around it"
        }

        require(indent >= 0) {
            "Indent must not be negative"
        }

        require(topLevelSpacing >= 0) {
            "Top-level spacing must not be negative"
        }

        require(innerSpacing >= 0) {
            "Inner spacing must not be negative"
        }

        indentString = " ".repeat(indent)
        topLevelSpacer = "\n".repeat(topLevelSpacing + 1)
        innerSpacer = "\n".repeat(innerSpacing + 1)
    }

    /**
     * Writes given [data] to a string as an improperties file.
     */
    public fun writeToString(data: Map<*, *>): String =
        data.toList().joinToString(topLevelSpacer) { (k, v) ->
            entryToString(k.toString(), v)
        } + "\n"

    /**
     * Writes given [data] to the file at a given path as an improperties file.
     *
     * @throws IOException if file at [path] cannot be written to.
     */
    public fun writeToFile(data: Map<*, *>, path: String) {
        writeToFile(data, File(path))
    }

    /**
     * Writes given [data] to the given file as an improperties file.
     *
     * @throws IOException if [file] cannot be written to.
     */
    public fun writeToFile(data: Map<*, *>, file: File) {
        file.writeText(writeToString(data))
    }

    private fun stringifyValue(value: Any?, inKeyValuePair: Boolean): String =
        when (value) {
            is Collection<*> -> {
                val text = listToString(value)

                if (inKeyValuePair)
                    " $text"
                else
                    text
            }
            is Map<*, *> -> {
                val text = mapToString(value.mapKeys { (k, _) -> k.toString() })

                if (inKeyValuePair)
                    " $text"
                else
                    text
            }

            else -> {
                if (inKeyValuePair)
                    "$keyValueJoiner$value"
                else
                    value.toString()
            }
        }

    private fun listToString(list: Collection<Any?>): String {
        val elements = list.joinToString(innerSpacer) { "- ${stringifyValue(it, false)}" }.prependIndent(indentString)

        return "$COMPOSITE_STARTER\n$elements\n$COMPOSITE_CLOSER"
    }

    private fun mapToString(map: Map<String, Any?>): String {
        val elements =
            map.toList().joinToString(innerSpacer) { (k, v) -> entryToString(k, v) }.prependIndent(indentString)

        return "$COMPOSITE_STARTER\n$elements\n$COMPOSITE_CLOSER"
    }


    private fun entryToString(key: String, value: Any?) = "$key${stringifyValue(value, true)}"

    /**
     * Builder for [ImpropertiesWriter]. Can be invoked without configuration for default values.
     */
    public class Builder {
        private var indent = DEFAULT_INDENT
        private var keyValueJoiner = DEFAULT_JOINER
        private var topLevelSpacing = DEFAULT_OUTER_SPACING
        private var innerSpacing = DEFAULT_INNER_SPACING

        /**
         * Sets indent for the resulting writer.
         *
         * @see ImpropertiesWriter
         */
        public fun indent(indent: Int): Builder = apply {
            this.indent = indent
        }

        /**
         * Sets key-value joiner for the resulting writer.
         *
         * @see ImpropertiesWriter
         */
        public fun keyValueJoiner(joiner: String): Builder = apply {
            this.keyValueJoiner = joiner
        }

        /**
         * Sets top-level spacing for the resulting writer.
         *
         * @see ImpropertiesWriter
         */
        public fun topLevelSpacing(spacing: Int): Builder = apply {
            this.topLevelSpacing = spacing
        }

        /**
         * Sets inner spacing for the resulting writer.
         *
         * @see ImpropertiesWriter
         */
        public fun innerSpacing(spacing: Int): Builder = apply {
            this.innerSpacing = spacing
        }

        /**
         * Creates an [ImpropertiesWriter] using the passed or default parameters.
         */
        public fun build(): ImpropertiesWriter =
            ImpropertiesWriter(indent, topLevelSpacing, innerSpacing, keyValueJoiner)
    }
}