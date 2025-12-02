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
import co.edu.udea.certificacion.innoSistemas.userinterfaces.DashboardPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

public class AuthenticationSteps {

    @Managed
    WebDriver driver;

    private Actor student;

    @Before
    public void setUp() {
        student = Actor.named("Student");
        student.can(BrowseTheWeb.with(driver));
    }

    @Given("the user is on the login page")
    public void userIsOnLoginPage() {
        student.attemptsTo(
                NavigateTo.theLoginPage());
    }

    @When("the user enters username {string}")
    public void userEntersUsername(String username) {
        student.attemptsTo(
                FillInputField.in(LoginPage.EMAIL_INPUT, username));
    }

    @When("the user enters password {string}")
    public void userEntersPassword(String password) {
        student.attemptsTo(
                FillInputField.in(LoginPage.PASSWORD_INPUT, password));
    }

    @When("the user clicks the login button")
    public void userClicksLoginButton() {
        student.attemptsTo(
                ClickOnElement.on(LoginPage.LOGIN_BUTTON));
    }

    @Then("the user should be redirected to the dashboard")
    public void userShouldBeRedirectedToDashboard() {
        student.should(
                seeThat(TheCurrentUrl.value(), containsString("/dashboard")));
    }

    @Then("the welcome message should be displayed")
    public void welcomeMessageShouldBeDisplayed() {
        student.should(
                seeThat(TheElementVisibility.of(DashboardPage.WELCOME_MESSAGE), is(true)));
    }

    @Then("an error message should be displayed")
    public void errorMessageShouldBeDisplayed() {
        student.should(
                seeThat(TheElementVisibility.of(LoginPage.ERROR_MESSAGE), is(true)));
    }

    @Then("the user should remain on the login page")
    public void userShouldRemainOnLoginPage() {
        student.should(
                seeThat(TheCurrentUrl.value(), containsString("/login")));
    }

    @Then("a validation message for username should appear")
    public void validationMessageForUsernameShouldAppear() {
        student.should(
                seeThat(TheElementVisibility.of(LoginPage.EMAIL_VALIDATION), is(true)));
    }

    @Then("the login button should be disabled")
    public void loginButtonShouldBeDisabled() {
        student.should(
                seeThat(TheElementState.isEnabled(LoginPage.LOGIN_BUTTON), is(false)));
    }
}
