package org.example

import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HtmlRenderer(
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
                    .chart-container {
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
                                style =
                                    "background-color: ${if (index % 2 == 0) color else color.replace("85%", "95%")};"
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

            h4 { +"Total avg: %.2f".format(gt.avgGrade) }
            h4 { +"Total ECTS: ${gt.sumEcts}" }

            div(classes = "chart-container") {
                canvas {
                    id = "myChart"
                    width = "400"
                    height = "400"
                }

                script {
                    unsafe {
                        +"""
                        const ctx = document.getElementById('myChart');
                        
                        const semesters = ${gt.bySemester.keys.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }};
                        const avgGrades = ${gt.bySemester.values.joinToString(prefix = "[", postfix = "]") { evaluationList ->
                                        evaluationList.map { it.grade.second }
                                            .filter { it != -1 }
                                            .average()
                                            .toString()
                                    }};
                        const summedEcts = ${gt.bySemester.values.joinToString(prefix = "[", postfix = "]") { evaluationList ->
                                        evaluationList.sumOf { it.ects }.toString()
                                    }};
                        
                        console.log(semesters);
                        console.log(avgGrades);
                        console.log(summedEcts);
            
                        new Chart(ctx, {
                            type: 'line',
                            data: {
                                labels: semesters,
                                datasets: [
                                    {
                                        label: 'Average Grade / semester',
                                        data: avgGrades,
                                        borderColor: 'red',
                                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                        yAxisID: 'y',
                                    },
                                    {
                                        label: 'ECTS / semester',
                                        data: summedEcts,
                                        borderColor: 'blue',
                                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                                        yAxisID: 'y1',
                                    }
                                ]
                            },
                            options: {
                                responsive: true,
                                scales: {
                                    y: {
                                        type: 'linear',
                                        position: 'left',
                                        min: 1,
                                        max: 5,
                                        reverse: true,
                                        ticks: {
                                            stepSize: 1
                                        },
                                        title: {
                                            display: true,
                                            text: 'Average Grade / semester',
                                        },
                                    },
                                    y1: {
                                        type: 'linear',
                                        position: 'right',
                                        grid: {
                                            drawOnChartArea: false, 
                                        },
                                        title: {
                                            display: true,
                                            text: 'ECTS / semester', // Add description
                                        },
                                    }
                                },
                            }
                        });
                    """.trimIndent()
                    }
                }
            }

            // Bar Chart Container
            div(classes = "chart-container") {
                canvas {
                    id = "barChart"
                    width = "400"
                    height = "400"
                }

                script {
                    unsafe {
                        +"""
                        const ctxBar = document.getElementById('barChart');
                        
                        const gradeLabels = ${gt.byGrade.keys.joinToString(prefix = "[", postfix = "]") { "\"${it.first}\"" }};
                        const gradeCounts = ${gt.byGrade.values.joinToString(prefix = "[", postfix = "]") { it.size.toString() }};
            
                        new Chart(ctxBar, {
                            type: 'bar',
                            data: {
                                labels: gradeLabels,
                                datasets: [
                                    {
                                        label: 'Grade Distribution',
                                        data: gradeCounts,
                                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                        borderColor: 'rgba(75, 192, 192, 1)',
                                        borderWidth: 1
                                    }
                                ]
                            },
                            options: {
                                responsive: true,
                                plugins: {
                                    title: {
                                        display: true,
                                        text: 'Grade Distribution'
                                    }
                                },
                                scales: {
                                    y: {
                                        beginAtZero: true,
                                        title: {
                                            display: true,
                                            text: 'Number of Evaluations'
                                        }
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
