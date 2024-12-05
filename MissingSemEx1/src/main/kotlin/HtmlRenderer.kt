package org.example

import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HtmlRenderer {
    fun generateHtml() = createHTML().html {
        head {
            title("Evaluation Results")
            style {
                +"""
                    /* Ensure the chart container does not exceed a maximum width */
                    #chart-container {
                        max-width: 500px;  /* Maximum width of the chart container */
                    }

                    canvas {
                        width: 100% !important;  /* Make the canvas responsive, fill its container */
                        height: auto !important;  /* Maintain aspect ratio if width changes */
                    }
                """.trimIndent()
            }
            link(rel = "stylesheet", href = "styles.css")
            script(src = "https://cdn.jsdelivr.net/npm/chart.js", type = "text/javascript") {}
        }
        body {
            h1 { +"Evaluation Results" }
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
