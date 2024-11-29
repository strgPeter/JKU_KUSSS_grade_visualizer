package org.example

import org.htmlunit.html.HtmlTable
import java.util.TreeSet

class GradeTable {
    private val table = TreeSet<Evaluation>()
    var avgGrade: Double = -1.0
    var sumEcts: Int = 0

    init {

        val scraper = Scraper()
        val htmlTable = scraper.getHtmlTable()

        if (htmlTable != null) {

            val rows = htmlTable.rows.filter { row ->
                row.cells.any { it.tagName.equals("td", ignoreCase = true) }
            }

            rows.forEach { println("Row data: ${it.asNormalizedText()}") }

            println(rows.count())

            avgGrade = table
                .filter { it.grade.second != -1 }
                .map { it.grade.second }
                .average()

            sumEcts = table.sumOf { it.ects }
        }

    }
}