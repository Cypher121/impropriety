package coffee.cypher.impropriety.configuration

/**
 * Reader configuration for improperties files.
 */
public class ReaderConfig {
    public companion object {
        @JvmField
        public val DEFAULT: ReaderConfig = ReaderConfig()
    }

    /**
     * Builder for [ReaderConfig]. Can be invoked without configuration for default values.
     */
    public class Builder {

        /**
         * Creates a [ReaderConfig] using the passed or default parameters.
         */
        public fun build(): ReaderConfig = ReaderConfig()
    }
}