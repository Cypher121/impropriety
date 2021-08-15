package coffee.cypher.impropriety.model

import kotlinx.serialization.Serializable

/**
 * An improperties file node. Will always be one of [ImpList], [ImpObject], [ImpValue], or [ImpMissing].
 * Has methods to convert to any of those, or traverse the file before specifying the final type.
 */

@Serializable
public sealed class ImpNode protected constructor(private val path: String, private val description: String) {

    /**
     * Returns whether the current node is an [ImpList].
     */
    public fun isList(): Boolean = this is ImpList

    /**
     * Returns whether the current node is an [ImpObject].
     */
    public fun isObject(): Boolean = this is ImpObject

    /**
     * Returns whether the current node is an [ImpValue].
     */
    public fun isValue(): Boolean = this is ImpValue

    /**
     * Returns whether the current node refers to an existing point on the tree.
     */
    public fun exists(): Boolean = this !is ImpMissing

    /**
     * Returns current node as an [ImpList] if it is one. Throws an exception otherwise.
     *
     * @throws IllegalArgumentException if the node is of an incorrect type.
     */
    public fun asList(): ImpList = tryConvert()

    /**
     * Returns current node as an [ImpObject] if it is one. Throws an exception otherwise.
     *
     * @throws IllegalArgumentException if the node is of an incorrect type.
     */
    public fun asObject(): ImpObject = tryConvert()

    /**
     * Returns current node as an [ImpValue] if it is one. Throws an exception otherwise.
     *
     * @throws IllegalArgumentException if the node is of an incorrect type.
     */
    public fun asValue(): ImpValue = tryConvert()

    /**
     * Returns current node as an [ImpMissing] if it is one. Throws an exception otherwise.
     *
     * @throws IllegalArgumentException if the node exists.
     */
    public fun missing(): ImpMissing = tryConvert()

    /**
     * Returns a node representing the element accessed by [key] in current node,
     * or [ImpMissing] if no such key exists or this node doesn't permit key-value indexing.
     */
    public open fun get(key: String): ImpNode = missing(keySubpath(key), "incorrect access")

    /**
     * Returns a node representing the element accessed by [index] in current node,
     * or [ImpMissing] if no such key exists or this node doesn't permit array indexing.
     */
    public open fun get(index: Int): ImpNode = missing(indexSubpath(index), "incorrect access")

    /**
     * Returns a string representation of the current node in relation to the tree root.
     */
    public fun path(): String = path

    @Suppress("UNCHECKED_CAST")
    protected fun Any.asNode(path: String): ImpNode = when (this) {
        is List<*> -> ImpList(path, this as List<Any>)
        is Map<*, *> -> ImpObject(path, this as Map<String, Any>)
        is String -> ImpValue(path, this)
        else -> throw IllegalArgumentException("Value of type ${this::class.java.simpleName} found in node tree, cannot convert")
    }

    protected fun missing(path: String, reason: String): ImpMissing = ImpMissing(path, this.path, reason)

    protected fun indexSubpath(index: Int): String = "$path[$index]"
    protected fun keySubpath(key: String): String = "$path.$key"

    private inline fun <reified T : ImpNode> tryConvert(): T =
        this as? T
            ?: throw IllegalArgumentException(
                "Node `${path()}` is $description, cannot convert to ${T::class.java.simpleName}"
            )
}