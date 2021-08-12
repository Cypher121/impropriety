package coffee.cypher.impropriety.model

import coffee.cypher.impropriety.ImpropertiesReader

/**
 * [ImpNode] variant representing a [List].
 *
 * @see ImpNode
 */
public class ImpList
internal constructor(path: String, private val list: List<Any>) :
    ImpNode(path, "a list") {

    /**
     * Returns the [List] representation of this subtree.
     *
     * @see ImpropertiesReader.toMap for possible types of elements.
     */
    public fun get(): List<Any> = list

    override fun get(index: Int): ImpNode =
        if (index in list.indices)
            list[index].asNode(indexSubpath(index))
        else
            missing(indexSubpath(index), "index out of bounds")
}