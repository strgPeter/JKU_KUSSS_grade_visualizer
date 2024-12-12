package org.example

import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Class representing a grade table.
 */
class GradeTable {

    private var table: List<Evaluation> = emptyList()
    val table_: List<Evaluation>
        get() = this.table.toList()

    val bySemester: Map<String, List<Evaluation>>
        get() = table.groupBy { it.semester }

    val byGrade: Map<Pair<String, Int>, List<Evaluation>>
        get() = table.groupBy { it.grade }.toSortedMap(compareBy { it.second })

    val avgGrade: Double
        get() = table
            .filter { it.grade.second != -1 }
            .map { it.grade.second }
            .average()

    val sumEcts: Double
        get() = table.sumOf { it.ects }

    val count: Int  get() = table.size

    private val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYYY_HHmmss"))

    /**
     * Initializes the grade table by scraping evaluations from an external source.
     * If an evaluation exists already the better grade is taken into account.
     */
    init {

        val scraper = Scraper()
        val htmlTable = scraper.getHtmlTable()

        if (htmlTable != null) {

            val rows = htmlTable.rows.filter { row ->
                row.cells.any { it.tagName.equals("td", ignoreCase = true) }
            }

            table = rows
                .mapNotNull { row ->
                    val cells = row.cells.map { it.asNormalizedText().trim() }
                    val evaluation = Evaluation(
                        lvaName = "${cells[1].substringBefore("(")}- ${cells[4]}",
                        lvaId = cells[1].substringBefore(",").substringAfter("("),
                        semester = cells[1].substringBefore(")").substringAfter(","),
                        grade = cells[2] to Evaluation.getGradeNumeric(cells[2]),
                        ects = cells[5].replace(',', '.').toDouble(),
                        date = extrDate(cells[0]),
                        id = cells[3]
                    )
                    evaluation
                }
                .fold(mutableListOf<Evaluation>()) { mListAccumul, newEval ->
                    val existingEval = mListAccumul.find { it.id == newEval.id }
                    if (existingEval == null) {
                        mListAccumul.add(newEval)
                    } else if (newEval.grade.second > existingEval.grade.second) {
                        mListAccumul.remove(existingEval)
                        mListAccumul.add(newEval)
                    }
                    mListAccumul
                }
                .sorted()
        }

    }

    /**
     * Renders the evaluations table in a readable text format.
     * Prints to std_out
     */
    fun renderTxt(){
        println("====Your Evaluations====\n")

        table.forEach { println(it) }
        println("\nTotal avg: %.2f".format(avgGrade))
        println("Total ECTS: ${sumEcts}\n")

        println("------By Semester------")
        bySemester.forEach { sem ->
            println("${sem.key}:")
            sem.value.forEach { println("\t$it") }
            println("\tAvg: %.2f".format(
                sem.value
                    .filter { it.grade.second != -1 }
                    .map { it.grade.second }
                    .average()
            ))
            println("\tECTS: ${sem.value.sumOf { it.ects }}")

            println()
        }

        println("--------By Grade--------")
        byGrade
            .forEach { grade ->
            println("${grade.key}:")
            grade.value.forEach { println("\t$it") }
            val n = grade.value.size.toDouble()
            println("\tCount: $n (%.2f)\n".format(n / count * 100))
        }

    }

    /**
     * Saves the evaluations table as a CSV file at the specified path.
     *
     * @param path the directory where the CSV file will be saved.
     */
    fun saveAsCsv(path: String){
        val csvFile = File(path, "grade_table_$timestamp.csv")

        csvFile.printWriter().use { writer ->
            writer.println("lvaName,lvaId,semester,gradeDescription,ects,date")

            // Write data rows
            table.forEach { eval ->
                writer.println(
                    "${eval.lvaName};" +
                            "${eval.lvaId};" +
                            "${eval.semester};" +
                            "${eval.grade.first};" +
                            "${eval.ects};" +
                            "${eval.date}"
                )
            }
        }

        println("CSV file saved at: ${csvFile.absolutePath}")
    }

    /**
     * Saves the evaluations table as an HTML file at the specified path.
     *
     * @param path the directory where the HTML file will be saved.
     */
    fun saveAsHtml(path: String) {
        val hr = HtmlRenderer(this)

        val htmlFile = File(path, "grades_$timestamp.html")

        htmlFile.printWriter().use { writer ->
            writer.println(hr.generateHtml() as String)
        }

        println("HTML file saved at: ${htmlFile.absolutePath}")
    }

    /**
     * Extracts a date from a string in the format "dd.MM.yyyy".
     *
     * @param s the string representing a date.
     * @return the parsed [LocalDate] object.
     */
    private fun extrDate (s: String) : LocalDate {
        val arr = s.split(".").map { it.toInt() }

        return LocalDate.of(arr[2], arr[1], arr[0])
    }
}