package coffee.cypher.impropriety

import coffee.cypher.impropriety.configuration.WriterConfig

data class ImpropertiesWriterFixture(
    val base: ImpropertiesFixture,
    val params: WriterConfig
) {
    companion object {
        @JvmStatic
        fun all() = listOf(SIMPLE_FIELDS, COMPOSITE_VALUES)

        @JvmField
        val SIMPLE_FIELDS = ImpropertiesWriterFixture(
            fixtureFromFiles("writing simple fields"),
            WriterConfig(
                indent = 2,
                topLevelSpacing = 1,
                innerSpacing = 0,
                keyValueJoiner = " : "
            )
        )

        @JvmField
        val COMPOSITE_VALUES = ImpropertiesWriterFixture(
            fixtureFromFiles("writing composite values"),
            WriterConfig(
                indent = 4,
                topLevelSpacing = 0,
                innerSpacing = 1,
                keyValueJoiner = " = "
            )
        )
    }
}