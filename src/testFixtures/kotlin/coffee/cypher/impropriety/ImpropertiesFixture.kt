package coffee.cypher.impropriety

data class ImpropertiesFixture(
    val name: String,
    val source: String,
    val data: Map<String, Any>
) {
    companion object {
        @JvmStatic
        fun all() = listOf(SIMPLE_FIELDS, COMPOSITE_VALUES, FULL_FEATURES)
        @JvmField
        val SIMPLE_FIELDS = fixtureFromFiles("simple fields")

        @JvmField
        val COMPOSITE_VALUES = fixtureFromFiles("composite values")

        @JvmField
        val FULL_FEATURES = fixtureFromFiles("full features")
    }
}