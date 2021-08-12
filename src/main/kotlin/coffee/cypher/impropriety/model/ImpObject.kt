package coffee.cypher.impropriety.model

import coffee.cypher.impropriety.ImpropertiesReader

/**
 * [ImpNode] variant representing a [Map].
 *
 * @see ImpNode
 */
public class ImpObject
internal constructor(path: String, private val map: Map<String, Any>) :
    ImpNode(path, "an object") {

    /**
     * Returns the [Map] representation of this subtree.
     *
     * @see ImpropertiesReader.toMap for possible types of values.
     */
    public fun get(): Map<String, Any> = map

    override fun get(key: String): ImpNode =
        if (key in map)
            map.getValue(key).asNode(keySubpath(key))
        else
            missing(keySubpath(key), "missing key")
}