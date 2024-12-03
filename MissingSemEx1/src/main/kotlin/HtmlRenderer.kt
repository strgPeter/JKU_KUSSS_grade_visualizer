package org.example

import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HtmlRenderer {
    fun generateHtml() = createHTML().html {
        head {
            title("Evaluation Results")
            link(rel = "stylesheet", href = "styles.css")
            script(src = "https://cdn.jsdelivr.net/npm/chart.js", type = "text/javascript") {}
            script(src = "chart.js", type = "text/javascript") {}
        }
        body {
            h1 { +"Evaluation Results" }
        }
    }
}