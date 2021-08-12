package coffee.cypher.impropriety.model

/**
 * [ImpNode] variant representing a terminal [String] value.
 *
 * @see ImpNode
 */
public class ImpValue
internal constructor(path: String, private val value: String) :
    ImpNode(path, "a value") {

    /**
     * Returns the [String] value of this node.
     */
    public fun get(): String = value
}