package org.example
import org.htmlunit.BrowserVersion
import org.htmlunit.DefaultCssErrorHandler
import org.htmlunit.WebClient
import org.htmlunit.cssparser.parser.CSSParseException
import org.htmlunit.html.*

import java.io.IOException
import java.util.Scanner
import java.util.logging.Level
import java.util.logging.Logger

class Scraper {

    fun getHtmlTable(): HtmlTable? =
        try {
            val wCli = WebClient(BrowserVersion.CHROME)
            Logger.getLogger("com.gargoylesoftware").level = Level.OFF
            Logger.getLogger("org.htmlunit").level = Level.OFF
            wCli.setIncorrectnessListener { _, _ -> }
            wCli.cssErrorHandler = object : DefaultCssErrorHandler() {
                override fun warning(exception: CSSParseException?) {}
                override fun error(exception: CSSParseException?) {}
                override fun fatalError(exception: CSSParseException?) {}
            }

            val kusssStartPage = wCli.getPage<HtmlPage>("https://kusss.jku.at/kusss/index.action")
            val shibbolethLoginPage = clickLogin(kusssStartPage)
            val kusssHomePage = shibbolethLogin(shibbolethLoginPage)
            //TODO: check if username and pswd are correct
            getGradeInfo(kusssHomePage)
        }catch (e: IOException){
            println("Something went wrong while scraping")
            println(e.message)
            null
        }

    @Throws(IOException::class)
    private fun clickLogin(page: HtmlPage): HtmlPage {
        val form = page.getFormByName("loginform")
        return form.getInputByValue<HtmlSubmitInput>("Login").click()
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

    private fun getGradeInfo(kusssHomePage: HtmlPage): HtmlTable {
        val resultsLink: HtmlAnchor = kusssHomePage.getByXPath<HtmlAnchor>("//a[span/text()='Results on my exams']")[0]
        return resultsLink.click<HtmlPage>().getByXPath<HtmlTable>("/html/body/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td/div/table[2]")[0]
    }

    private fun readInput(msg: String): String {
        val scanner = Scanner(System.`in`)
        print(msg)
        return scanner.nextLine().trim()
    }
}
