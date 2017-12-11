package io.opencubes.dashagent

internal class Header(val name: String, val values: MutableList<String>) {
    constructor(name: String, vararg values: String) : this(name, values.toMutableList())
    val value: String get() = values.joinToString(",")

    fun add(vararg values: String) = this.values.addAll(values)

    operator fun component1() = name
    operator fun component2() = value
}
