package ru.skillbranch.kotlinexample.extensions

fun  String.loginNormalized() : String {
    var result = this.trim().toLowerCase()
    if (this.startsWith('+')) {
        result = this.replace("""[^+\d]""".toRegex(), "")
    }
    return result
}

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean) :List<T> =
    when {
        !this.any(predicate) -> this
        else -> this.dropLast(1).dropLastUntil(predicate)
    }
