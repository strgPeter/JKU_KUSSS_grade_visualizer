package org.example

import java.util.Date

data class Evaluation (
    val lvaName: String,
    val lvaId: String,
    val semester: String,
    val grade: Pair<String, Int>,
    val ects: Int,
    val date: Date,
    val id: String
) : Comparable<Evaluation>{
    
    private fun getGradeNumeric(g: String): Int {
        return -1
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
        return "$lvaName: ${grade.first}, $ects ECTS ($date)"
    }


}
