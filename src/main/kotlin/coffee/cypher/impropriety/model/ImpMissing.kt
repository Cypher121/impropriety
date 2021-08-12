package coffee.cypher.impropriety.model

/**
 * [ImpNode] variant representing a missing value.
 *
 * @see ImpNode
 */
public class ImpMissing
internal constructor(path: String, private val missingAfter: String, private val reason: String) :
    ImpNode(path, "missing ($reason on `$missingAfter`)") {

    /**
     * Returns a reason for this value's absence, including its last known parent in the tree.
     */
    public fun why(): String = "$reason on `$missingAfter`"
}