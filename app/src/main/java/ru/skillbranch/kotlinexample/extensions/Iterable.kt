package ru.skillbranch.kotlinexample.extensions

fun  String.loginNormalized() : String {
    var result = this.trim().toLowerCase()
    if (this.startsWith('+')) {
        result = this.replace("""[^+\d]""".toRegex(), "")
    }
    return result
}

//fun CharSequence.any(predicate: (Char) -> Boolean): Boolean {}

//fun List.dropLastUntil(predicate: (T) -> Boolean) : List {}