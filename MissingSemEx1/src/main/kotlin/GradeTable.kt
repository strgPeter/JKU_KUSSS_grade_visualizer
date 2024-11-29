package org.example

import java.time.LocalDate

class GradeTable {
    private lateinit var table: List<Evaluation>
    var avgGrade: Double = -1.0
    var sumEcts: Double = 0.0

    init {

        val scraper = Scraper()
        val htmlTable = scraper.getHtmlTable()

        if (htmlTable != null) {

            val rows = htmlTable.rows.filter { row ->
                row.cells.any { it.tagName.equals("td", ignoreCase = true) }
            }

            table = rows
                .map { row ->
                    val cells = row.cells.map { it.asNormalizedText().trim() }
                    Evaluation(
                        lvaName = "${cells[1].substringBefore("(")}- ${cells[4]}",
                        lvaId = cells[1].substringBefore(",").substringAfter("("),
                        semester = cells[1].substringBefore(")").substringAfter(","),
                        grade = cells[2] to Evaluation.getGradeNumeric(cells[2]),
                        ects = cells[5].replace(',', '.').toDouble(),
                        date = extrDate(cells[0]),
                        id = cells[3]
                    )}
                .sorted()

            table.forEach { println(it) }

            avgGrade = table
                .filter { it.grade.second != -1 }
                .map { it.grade.second }
                .average()

            sumEcts = table.sumOf { it.ects }
        }

    }

    private fun extrDate (s: String) : LocalDate {
        val arr = s.split(".").map { it.toInt() }

        return LocalDate.of(arr[2], arr[1], arr[0])
    }
}