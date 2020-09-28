package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): List<List<Pair<Int, Int>>> {
    val outList  = mutableListOf<List<Pair<Int,Int>>>()
    bounds.forEach { (l,h) ->
        run {
            val innerBounds =
                this.filter { (lb, hb) -> lb >= l && hb <= h }
            outList.add(innerBounds)
        }
    }
    return  outList
}
