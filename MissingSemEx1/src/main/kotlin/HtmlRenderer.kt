package org.example

import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HtmlRenderer (
    val gt: GradeTable
) {
    fun generateHtml() = createHTML().html {

        // Map to store unique colors for each semester <semester, color>
        val semesterColors = mutableMapOf<String, String>()

        fun getColor(sem: String): String {
            semesterColors[sem]?.let { return it }

            var color: String
            do {
                val hue = (Math.random() * 360).toInt()
                color = "hsl($hue, 70%, 85%)"
            } while (semesterColors.containsValue(color))

            semesterColors.put(sem, color)
            return color
        }

        head {
            title("Evaluation Results")
            style {
                +"""
                    /* General styling */
                    body {
                        font-family: Arial, sans-serif;
                        margin: 20px;
                        line-height: 1.6;
                    }
                    
                    /* Ensure the chart container does not exceed a maximum width */
                    #chart-container {
                        max-width: 500px;  /* Maximum width of the chart container */
                    }

                    canvas {
                        width: 100% !important;  /* Make the canvas responsive, fill its container */
                        height: auto !important;  /* Maintain aspect ratio if width changes */
                    }
                    
                    /* Table styling */
                    table {
                        border-collapse: collapse;
                        width: auto;
                        margin-bottom: 40px; /* Add spacing between table and chart */
                        margin-left: 0; /* Align table to the left */
                    }

                    th, td {
                        text-align: left;
                        padding: 8px;
                        border: 1px solid #ddd;
                    }

                    th {
                        background-color: #f4f4f4;
                    }

                    tr:nth-child(even) {
                        background-color: #f9f9f9; /* Default row color */
                    }
                """.trimIndent()
            }
            link(rel = "stylesheet", href = "styles.css")
            script(src = "https://cdn.jsdelivr.net/npm/chart.js", type = "text/javascript") {}
        }
        body {

            h1 { +"Evaluation Results" }

            div {
                table {
                    thead {
                        tr {
                            th { +"LVA Name" }
                            th { +"Semester" }
                            th { +"Grade" }
                            th { +"ECTS" }
                            th { +"Date" }
                        }
                    }
                    tbody {
                        val semesterColors = mutableMapOf<String, Pair<String, String>>()

                        gt.table_.forEachIndexed { index, evaluation ->

                            val color = getColor(evaluation.semester)

                            tr {
                                style = "background-color: ${if (index % 2 == 0) color else color.replace("85%", "95%")};"
                                td { +evaluation.lvaName }
                                td { +evaluation.semester }
                                td { +evaluation.grade.first }
                                td { +"${evaluation.ects}" }
                                td { +evaluation.date.toString() }
                            }
                        }
                    }
                }
            }

            h4 {
                +"Total avg: %.2f".format(gt.avgGrade)
            }

            h4 {
                +"Total ECTS: ${gt.sumEcts}"
            }

            // Add a canvas element for the chart
            div {
                id = "chart-container"
                canvas {
                    id = "myChart"
                    width = "400"
                    height = "400"
                }

                script {
                    unsafe {
                        +"""
                            const ctx = document.getElementById('myChart');

                            new Chart(ctx, {
                                type: 'bar',
                                data: {
                                    labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                                    datasets: [{
                                        label: '# of Votes',
                                        data: [12, 19, 3, 5, 2, 3],
                                        borderWidth: 1
                                    }]
                                },
                                options: {
                                    scales: {
                                        y: {
                                            beginAtZero: true
                                        }
                                    }
                                }
                            });
                        """.trimIndent()
                    }
                }
            }
        }
    }
}
