package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
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

    @Then("the login button should be disabled")
    public void loginButtonShouldBeDisabled() {
        student.should(
                seeThat(TheElementState.isEnabled(LoginPage.LOGIN_BUTTON), is(false)));
    }

    @When("I enter valid credentials")
    public void iEnterValidCredentials() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        student.attemptsTo(NavigateTo.theLoginPage());
        throw new PendingException();
    }

    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToTheDashboard() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("I enter invalid credentials")
    public void iEnterInvalidCredentials() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("I should see an error message indicating invalid login")
    public void iShouldSeeAnErrorMessageIndicatingInvalidLogin() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("and my account is not blocked")
    public void andMyAccountIsNotBlocked() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("I send invalid credentials three times")
    public void iSendInvalidCredentialsThreeTimes() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("I log in with valid credentials")
    public void iLogInWithValidCredentials() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("I log out")
    public void iLogOut() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the attempt counter should be reseted")
    public void theAttemptCounterShouldBeReseted() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("I enter the email {string} and the password {string}")
    public void iEnterTheEmailAndThePassword(String arg0, String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("I send the credentials")
    public void iSendTheCredentials() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }


    @Then("the message {string} should be displayed")
    public void theMessageShouldBeDisplayed(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("my account is not blocked")
    public void myAccountIsNotBlocked() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("I send invalid credentials four more times")
    public void iSendInvalidCredentialsFourMoreTimes() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("my account should be blocked")
    public void myAccountShouldBeBlocked() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("I should see a message indicating my account is locked")
    public void iShouldSeeAMessageIndicatingMyAccountIsLocked() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
