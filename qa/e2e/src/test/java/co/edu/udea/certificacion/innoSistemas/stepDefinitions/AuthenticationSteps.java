package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import co.edu.udea.certificacion.innoSistemas.tasks.*;
import co.edu.udea.certificacion.innoSistemas.questions.*;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
import net.serenitybdd.screenplay.waits.WaitUntil;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.*;

public class AuthenticationSteps {

    @Managed
    WebDriver driver;

    private static final int STEP_WAIT_MS = 1200;

    private Actor user;

    @Before
    public void setUp() {
        user = Actor.named("User");
        user.can(BrowseTheWeb.with(driver));
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        user.attemptsTo(NavigateTo.theLoginPage());
    }

    @When("I enter the email {string} and the password {string}")
    public void iEnterTheEmailAndThePassword(String email, String password) {
        user.attemptsTo(EnterEmailAndPassword.with(email, password));
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        user.attemptsTo(ClickLoginButton.now());
    }

    @Then("I should see the error message")
    public void iShouldSeeTheErrorMessage() {
        user.attemptsTo(
            WaitUntil.the(LoginPage.ERROR_MESSAGE, isVisible()).forNoMoreThan(10).seconds()
        );
        user.should(
            seeThat(TheElementVisibility.of(LoginPage.ERROR_MESSAGE), is(true))
        );
    }

    @Then("I should see the user name displayed")
    public void iShouldSeeTheUserNameDisplayed() {
        user.attemptsTo(
            WaitUntil.the(LoginPage.USER_NAME, isVisible()).forNoMoreThan(15).seconds()
        );
        user.should(
            seeThat(TheElementVisibility.of(LoginPage.USER_NAME), is(true))
        );
    }
}
