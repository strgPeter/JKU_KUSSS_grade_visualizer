package org.example
import org.htmlunit.BrowserVersion
import org.htmlunit.Page
import org.htmlunit.WebClient
import org.htmlunit.html.*

import java.io.IOException
import java.util.Scanner

class Scraper {

    fun getHtmlPage(): String {

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        return try {
            WebClient(BrowserVersion.CHROME).use { wc ->
                val kusssStartPage = wc.getPage<HtmlPage>("https://kusss.jku.at/kusss/index.action")
                val shibbolethLoginPage = clickLogin(kusssStartPage)
                val kusssHomePage = shibbolethLogin(shibbolethLoginPage)
                val kusssGradeInfoPage = getGradeInfoPage(kusssHomePage)
                kusssGradeInfoPage.asNormalizedText()
            }
        } catch (e: IOException) {
            ""
        }
    }

    @Throws(IOException::class)
    private fun clickLogin(page: HtmlPage): HtmlPage {
        val form = page.getFormByName("loginform")
        val login = form.getInputByValue<HtmlSubmitInput>("Login")
        return login.click()
    }

    @Throws(IOException::class)
    private fun shibbolethLogin(loginPage: HtmlPage): HtmlPage {
        val form = loginPage.forms[0]

        val usernameField = form.getInputByName<HtmlTextInput>("j_username")
        val passwordField = form.getInputByName<HtmlInput>("j_password")
        val dontRememberLogin = form.getInputByName<HtmlCheckBoxInput>("donotcache")
        val submitButton = form.getInputByValue<HtmlSubmitInput>("Login")

        usernameField.valueAttribute = readInput("Enter username: ")
        passwordField.valueAttribute = readInput("Enter password: ")
        dontRememberLogin.isChecked = true
        return submitButton.click()
    }

    private fun getGradeInfoPage(kusssHomePage: HtmlPage): HtmlPage {
        val resultsLink: HtmlAnchor? = kusssHomePage.getByXPath<HtmlAnchor>(
            "//a[span/text()='Results on my exams']"
        ).firstOrNull()

        //TODO: change return of parameter when no result found
        val resultsPage: HtmlPage = resultsLink?.click() ?: kusssHomePage

        val table: HtmlTable? =
            resultsPage.getByXPath<HtmlTable>("/html/body/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/div/table[2]")
                .firstOrNull()

        if (table != null) {
            // Iterate over the rows of the table
            for (row: HtmlTableRow in table.rows) {
                println("Found row:")
                // Iterate over the cells of the row
                for (cell: HtmlTableCell in row.cells) {
                    println("   Found cell: ${cell.asNormalizedText()}")
                }
            }
        } else {
            println("Table not found!")
        }

        return resultsPage
    }

    private fun readInput(msg: String): String {
        print(msg)
        val scanner = Scanner(System.`in`)
        return scanner.nextLine()
    }
}
