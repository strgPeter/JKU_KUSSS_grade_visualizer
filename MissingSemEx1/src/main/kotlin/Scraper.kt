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

/**
 * Class responsible for scraping data from the KUSSS system using HtmlUnit.
 */
class Scraper {

    /**
     * Retrieves the grade information table from the KUSSS system.
     *
     * @return the grade information as an [HtmlTable] or null if an error occurs.
     */
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

            val errorMessages = kusssHomePage.getByXPath<HtmlElement>(
                "//section/p[contains(@class, 'form-element') and contains(@class, 'form-error')]"
            )

            if (errorMessages.isNotEmpty()) {
                println("Error message(s) found:")
                errorMessages.forEach { println(it.asNormalizedText()) }
                throw Exception("Invalid login")
            }

            getGradeInfo(kusssHomePage)
        }catch (e: IOException){
            println("Something went wrong while scraping")
            println(e.message)
            null
        }catch (e: Exception){
            println(e.message)
            null
        }

    /**
     * Navigates to the login page by submitting the login form.
     *
     * @param page the initial KUSSS start page.
     * @return the Shibboleth login page.
     * @throws IOException if an error occurs during navigation.
     */
    @Throws(IOException::class)
    private fun clickLogin(page: HtmlPage): HtmlPage {
        val form = page.getFormByName("loginform")
        return form.getInputByValue<HtmlSubmitInput>("Login").click()
    }

    /**
     * Performs the Shibboleth login by filling out the form with user credentials.
     * THIS FUNCTION HAS BEEN UPDATED FOR THE NEW LOGIN PAGE STRUCTURE.
     *
     * @param loginPage the Shibboleth login page.
     * @return the KUSSS home page after successful login.
     * @throws IOException if an error occurs during login.
     */
    @Throws(IOException::class)
    private fun shibbolethLogin(loginPage: HtmlPage): HtmlPage {
        // Find the login form. Let's assume it's still the first form,
        // or find it more robustly if possible (e.g., by ID if it has one).
        val form = loginPage.forms.firstOrNull()
            ?: throw IOException("Could not find login form on page: ${loginPage.url}")

        // Find elements by name
        val usernameField = form.getInputByName<HtmlTextInput>("j_username")
        val passwordField = form.getInputByName<HtmlPasswordInput>("j_password")
        val dontRememberLogin = form.getInputByName<HtmlCheckBoxInput>("donotcache")

        // Find the submit button. It might be an <input type="submit"> or a <button type="submit">.
        var submitElement: HtmlElement? = form.querySelector<HtmlButton>("button[type='submit']")

        if (submitElement == null) {
            // If no <button type="submit">, try <input type="submit">
            submitElement = form.querySelector<HtmlSubmitInput>("input[type='submit']")
        }

        // Ensure a submit element was found
        val submitButton = submitElement
            ?: throw IOException("Could not find submit button (<button> or <input>) on login page: ${loginPage.url}")

        usernameField.valueAttribute = readInput("Enter username: ")
        passwordField.valueAttribute = readInput("Enter password: ")
        dontRememberLogin.isChecked = true

        return submitButton.click()
    }

    /**
     * Retrieves the grades table from the KUSSS home page.
     *
     * @param kusssHomePage the KUSSS home page after login.
     * @return the grades table as an [HtmlTable].
     */
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
