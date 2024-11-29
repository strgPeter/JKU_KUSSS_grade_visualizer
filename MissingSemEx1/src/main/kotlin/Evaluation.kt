package org.example

import java.time.LocalDate

data class Evaluation (
    val lvaName: String,
    val lvaId: String,
    val semester: String,
    val grade: Pair<String, Int>,
    val ects: Double,
    val date: LocalDate,
    val id: String
) : Comparable<Evaluation>{

    companion object{
        fun getGradeNumeric(g: String): Int {
            return when (g) {
                "sehr gut" -> 1
                "gut" -> 2
                "befriedigend" -> 3
                "genügend" -> 4
                "ungenügend" -> 5
                else -> -1 //mit erfolg Teilgenommen
            }
        }
    }

    override fun compareTo(other: Evaluation): Int {
        if (this.semester.takeWhile { it.isDigit() }.toInt() > other.semester.takeWhile { it.isDigit() }.toInt()) {
            return 1
        }else if (this.semester.takeWhile { it.isDigit() }.toInt() < other.semester.takeWhile { it.isDigit() }.toInt()){
            return -1
        }else{
            val a = this.semester.last().compareTo(other.semester.last())
            if (a != 0) return a
            return this.lvaName.compareTo(other.lvaName, true)
        }
    }

    override fun toString(): String {
        return "($semester)-$lvaName: ${grade.first}, $ects ECTS ($date)"
    }


}
