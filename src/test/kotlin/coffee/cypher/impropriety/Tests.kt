package coffee.cypher.impropriety

import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.GroupBody
import kotlin.test.assertEquals

object Tests : Spek({
    group("reader tests") {
        ImpropertiesFixture.all().forEach {
            testForFixture(it)
        }
    }

    group("writer tests") {
        ImpropertiesWriterFixture.all().forEach {
            testForFixture(it)
        }
    }
})

fun GroupBody.testForFixture(fixture: ImpropertiesFixture) {
    test(fixture.name) {
        val map = ImpropertiesReader.fromText(fixture.source).toMap()

        assertEquals(fixture.data, map, "Read result for fixture did not match expected")
    }
}

fun GroupBody.testForFixture(fixture: ImpropertiesWriterFixture) {
    fun String.forComparison() = lines().joinToString("") {
        if (it.isBlank())
            "\n"
        else
            "$it\n"
    }

    test(fixture.base.name) {
        val result = fixture.params.makeWriter().writeToString(fixture.base.data)

        assertEquals(
            fixture.base.source.forComparison(),
            result.forComparison(),
            "Write result for fixture did not match expected"
        )
    }
}