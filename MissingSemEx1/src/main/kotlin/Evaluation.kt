package org.example

import java.util.Date

data class Evaluation (
    val lvaName: String,
    val lvaId: String,
    val semester: String,
    val grade: Pair<String, Int>,
    val date: Date,
    val id: String
) : Comparable<Evaluation>{
    
    private fun getGradeNumeric(g: String): Int {
        TODO("Not yet implemented")
    }

    override fun compareTo(other: Evaluation): Int {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        TODO("Not yet implemented")
        return super.toString()
    }


}
