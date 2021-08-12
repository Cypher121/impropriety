package coffee.cypher.impropriety

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

fun ImpropertiesWriterFixture.Params.makeWriter() = ImpropertiesWriter(
    indent = indent,
    keyValueJoiner = keyValueJoiner,
    topLevelSpacing = topLevelSpacing,
    innerSpacing = innerSpacing
)

private class Marker

private val mapper = jacksonObjectMapper()

internal fun textFromFile(path: String) =
    Marker::class.java.getResourceAsStream(path)!!.bufferedReader().readText()

internal fun jsonFromFile(path: String): Map<String, Any> =
    mapper.readValue(textFromFile(path))

internal fun fixtureFromFiles(name: String) = name.replace(" ", "_").let { path ->
    ImpropertiesFixture(
        name,
        textFromFile("/fixtures/$path/fixture.improperties"),
        jsonFromFile("/fixtures/$path/fixture.json")
    )
}