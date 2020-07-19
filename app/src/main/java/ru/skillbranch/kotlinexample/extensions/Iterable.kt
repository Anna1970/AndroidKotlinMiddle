package ru.skillbranch.kotlinexample.extensions

fun  String.loginNormalized() : String {
    var result = this.trim().toLowerCase()
    if (this.startsWith('+')) {
        result = this.replace("""[^+\d]""".toRegex(), "")
    }
    return result
}

//fun List.dropLastUntil()