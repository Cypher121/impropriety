package coffee.cypher.impropriety

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


private class Marker

internal fun textFromFile(path: String) =
    Marker::class.java.getResourceAsStream(path)!!.bufferedReader().readText()

internal fun jsonFromFile(path: String): Map<String, Any> =
    Json.decodeFromString(textFromFile(path))

internal fun fixtureFromFiles(name: String) = name.replace(" ", "_").let { path ->
    ImpropertiesFixture(
        name,
        textFromFile("/fixtures/$path/fixture.improperties"),
        jsonFromFile("/fixtures/$path/fixture.json")
    )
}