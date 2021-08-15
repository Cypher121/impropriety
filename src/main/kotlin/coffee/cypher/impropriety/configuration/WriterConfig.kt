package coffee.cypher.impropriety.configuration

private const val DEFAULT_INDENT = 2
private const val DEFAULT_JOINER = " = "
private const val DEFAULT_OUTER_SPACING = 0
private const val DEFAULT_INNER_SPACING = 0

/**
 * Writer configuration for improperties files.
 *
 * @param indent configures indent for nested elements inside lists or objects.
 *
 * @param topLevelSpacing configures the number of blank lines between elements at the top level of the file.
 *
 * @param innerSpacing configures the number of blank lines between nested elements inside lists or objects.
 *
 * @param keyValueJoiner configures the token used for key-value pairs.
 */
public data class WriterConfig(
    val indent: Int = DEFAULT_INDENT,
    val topLevelSpacing: Int = DEFAULT_OUTER_SPACING,
    val innerSpacing: Int = DEFAULT_INNER_SPACING,
    val keyValueJoiner: String = DEFAULT_JOINER
) {
    public companion object {
        @JvmField
        public val DEFAULT: WriterConfig = WriterConfig()
    }

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
    }

    internal val indentString = " ".repeat(indent)
    internal val topLevelSpacer = "\n".repeat(topLevelSpacing + 1)
    internal val innerSpacer = "\n".repeat(innerSpacing + 1)

    /**
     * Builder for [WriterConfig]. Can be invoked without configuration for default values.
     */
    public class Builder {
        public var indent: Int = DEFAULT_INDENT
        public var topLevelSpacing: Int = DEFAULT_OUTER_SPACING
        public var innerSpacing: Int = DEFAULT_INNER_SPACING
        public var keyValueJoiner: String = DEFAULT_JOINER

        /**
         * Sets indent for the resulting writer config.
         *
         * @see WriterConfig
         */
        public fun indent(indent: Int): Builder = apply {
            this.indent = indent
        }

        /**
         * Sets top-level spacing for the resulting writer config.
         *
         * @see WriterConfig
         */
        public fun topLevelSpacing(topLevelSpacing: Int): Builder = apply {
            this.topLevelSpacing = topLevelSpacing
        }

        /**
         * Sets inner spacing for the resulting writer config.
         *
         * @see WriterConfig
         */
        public fun innerSpacing(innerSpacing: Int): Builder = apply {
            this.innerSpacing = innerSpacing
        }

        /**
         * Sets key-value joiner for the resulting writer config.
         *
         * @see WriterConfig
         */
        public fun keyValueJoiner(keyValueJoiner: String): Builder = apply {
            this.keyValueJoiner = keyValueJoiner
        }

        /**
         * Creates a [WriterConfig] using the passed or default parameters.
         */
        public fun build(): WriterConfig = WriterConfig(
            indent = indent,
            topLevelSpacing = topLevelSpacing,
            innerSpacing = innerSpacing,
            keyValueJoiner = keyValueJoiner
        )
    }
}
