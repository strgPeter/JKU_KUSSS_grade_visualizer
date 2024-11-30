package org.example

import java.util.Date

fun main(args: Array<String>) {
    args.forEach {
        println(it)
    }
    val gt = GradeTable()
    gt.renderTxt()
}
