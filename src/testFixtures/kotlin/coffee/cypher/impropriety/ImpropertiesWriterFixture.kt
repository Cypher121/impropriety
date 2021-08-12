package coffee.cypher.impropriety

data class ImpropertiesWriterFixture(
    val base: ImpropertiesFixture,
    val params: Params
) {
    companion object {
        @JvmStatic
        fun all() = listOf(SIMPLE_FIELDS, COMPOSITE_VALUES)

        @JvmField
        val SIMPLE_FIELDS = ImpropertiesWriterFixture(
            fixtureFromFiles("writing simple fields"),
            Params(
                indent = 2,
                topLevelSpacing = 1,
                innerSpacing = 0,
                keyValueJoiner = " : "
            )
        )

        @JvmField
        val COMPOSITE_VALUES = ImpropertiesWriterFixture(
            fixtureFromFiles("writing composite values"),
            Params(
                indent = 4,
                topLevelSpacing = 0,
                innerSpacing = 1,
                keyValueJoiner = " = "
            )
        )
    }

    data class Params(
        val indent: Int,
        val keyValueJoiner: String,
        val topLevelSpacing: Int,
        val innerSpacing: Int
    )
}