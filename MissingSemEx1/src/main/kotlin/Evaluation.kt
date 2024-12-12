package org.example

import java.time.LocalDate

/**
 * Represents an evaluation containing details such as course name, ID, semester, grade, ECTS points, date, and a unique identifier.
 * Implements [Comparable] to allow sorting based on semester and course name.
 */
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
        /**
         * Converts a grade description into its numeric equivalent.
         *
         * @param g the grade description as a string (e.g., "sehr gut").
         * @return the numeric equivalent of the grade, or -1 for unrecognized descriptions.
         */
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

    /**
     * Sorting is performed first by semester (numerically and alphabetically),
     * and then by course name (case-insensitive).
     *
     * @param other the other evaluation to compare to.
     * @return a negative integer, zero, or a positive integer if this evaluation
     *         is less than, equal to, or greater than the other.
     */
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
