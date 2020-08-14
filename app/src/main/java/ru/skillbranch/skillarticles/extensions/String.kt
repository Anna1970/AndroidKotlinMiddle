package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    if (this.isNullOrEmpty() || substr.isNullOrEmpty()) return emptyList()
    var position : Int = this!!.indexOf(substr, 0, ignoreCase)
    if (position == -1) return emptyList()
    var list = mutableListOf<Int>()
    do {
        list.add(position)
        position = this!!.indexOf(substr, position + 1, ignoreCase)
    } while (position != -1)

    return list
}