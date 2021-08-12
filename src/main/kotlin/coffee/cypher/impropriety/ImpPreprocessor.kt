package coffee.cypher.impropriety

internal object ImpPreprocessor {
    private fun processLine(input: String): String =
        input
            .let(this::splitCompositeStarter)
            .let(String::trimStart)

    fun process(input: String): String =
        input
            .lines()
            .joinToString("\n", transform = this::processLine)
            .let(this::ignoreEscapedNewlines)
            .let(this::appendNewlineToEnd)

    private fun appendNewlineToEnd(input: String) =
        if (input.endsWith('\n'))
            input
        else
            input + '\n'

    private fun ignoreEscapedNewlines(input: String) = input.replace("\\\\\n".toRegex(), "")

    private fun splitCompositeStarter(input: String): String = input.replace("([^\\\\\\s])->\\s*$".toRegex(), "\$1 ->")
}