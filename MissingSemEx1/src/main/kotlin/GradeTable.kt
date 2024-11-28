package org.example

import java.util.TreeSet

class GradeTable {
    val table = TreeSet<Evaluation>(compareBy { it.date })
    init {

    }
}