package org.example

fun main() {
    println("Hello World!")
    val s = Scraper()
    val page = s.getHtmlPage()
    println(page)
}