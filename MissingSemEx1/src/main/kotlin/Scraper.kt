import org.htmlunit.BrowserVersion;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;

import java.io.IOException;
import java.util.Scanner;

public class Scraper {
    public String getHtmlPage() {
        try (final WebClient wc = new WebClient(BrowserVersion.CHROME)){
            final HtmlPage kusssStartPage = wc.getPage("https://kusss.jku.at/kusss/index.action");

            final HtmlPage loginpage = clickLogin(kusssStartPage);

            return shibbolethLogin(loginpage).asNormalizedText();
        } catch (IOException e) {
            return "";
        }
    }

    private HtmlPage clickLogin(HtmlPage page) throws IOException {
        final HtmlForm form = page.getFormByName("loginform");
        final HtmlSubmitInput login = form.getInputByValue("Login");
        return login.click();
    }

    private HtmlPage shibbolethLogin(HtmlPage loginpage) throws IOException {
        HtmlForm form = loginpage.getForms().get(0);

        HtmlTextInput usernameField = form.getInputByName("j_username");
        HtmlInput passwordField = form.getInputByName("j_password");
        HtmlCheckBoxInput dontRememberLogin = form.getInputByName("donotcache");
        HtmlSubmitInput submitButton = form.getInputByValue("Login");

        usernameField.setValueAttribute(readInput("Enter username: "));
        passwordField.setValueAttribute(readInput("Enter password: "));
        dontRememberLogin.setChecked(true);
        HtmlPage resultPage = submitButton.click();
        return resultPage;
    }

    private static String readInput(String msg){
        //TODO: do some checks idk
        System.out.print(msg);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

}
