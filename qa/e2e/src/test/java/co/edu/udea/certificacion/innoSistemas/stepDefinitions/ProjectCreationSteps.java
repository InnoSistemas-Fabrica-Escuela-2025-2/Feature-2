package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import co.edu.udea.certificacion.innoSistemas.tasks.*;
import co.edu.udea.certificacion.innoSistemas.questions.TheElementVisibility;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
// Using Tasks instead of direct Interactions
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProjectCreationSteps {

    @Managed
    WebDriver driver;

    private static final int STEP_WAIT_MS = 1200;

    private Actor student;
    private Project currentProject;

    @Before
    public void setUp() {
        student = Actor.named("Student");
        student.can(BrowseTheWeb.with(driver));
    }

    @Given("the user {string} is authenticated")
    public void userIsAuthenticated(String username) {
        student.attemptsTo(
                NavigateTo.theLoginPage(),
                Login.withCredentials(username, "geraldin"));
        student.attemptsTo(
                WaitUntil.the(LoginPage.USER_NAME, isVisible()).forNoMoreThan(20).seconds()
        );
        WaitTime.putWaitTimeOf(STEP_WAIT_MS);
    }

    @Given("the user is on the projects page")
    public void userIsOnProjectsPage() {
        student.attemptsTo(NavigateTo.theProjectsPage());
        student.attemptsTo(
                WaitUntil.the(ProjectsPage.CREATE_PROJECT_BUTTON, isVisible()).forNoMoreThan(20).seconds()
        );
        WaitTime.putWaitTimeOf(STEP_WAIT_MS);
    }

    @When("the user clicks on create project button")
    public void userClicksCreateProjectButton() {
        student.attemptsTo(ClickCreateProjectButton.now());
    }

    @When("the user fills the complete project form with:")
    public void userFillsCompleteProjectForm(DataTable dataTable) {
        currentProject = Project.fromDataTable(dataTable);
        student.attemptsTo(FillCompleteProjectForm.with(currentProject));
    }

    @When("the user fills partial project form with:")
    public void userFillsPartialProjectForm(DataTable dataTable) {
        currentProject = Project.fromDataTable(dataTable);
        student.attemptsTo(FillPartialProjectForm.with(currentProject));
    }

    @Then("a validation error should be displayed with message {string}")
    public void validationErrorShouldBeDisplayedWithMessage(String expectedMessage) {
        WaitTime.putWaitTimeOf(STEP_WAIT_MS);
        student.should(seeThat(TheElementVisibility.of(ProjectsPage.VALIDATION_ERROR), is(true)));
    }

    @Then("the user clicks cancel button")
    public void userClicksCancelButton() {
        student.attemptsTo(ClickCancelProjectButton.now());
    }

    @Then("the user confirms cancel action")
    public void userConfirmsCancelAction() {
        student.attemptsTo(ConfirmCancelProject.now());
    }

    @When("the user submits the project form")
    public void userSubmitsProjectForm() {
        student.attemptsTo(SubmitProjectForm.now());
    }

    @Then("the project should appear in the projects list")
    public void projectShouldAppearInList() {
        WaitTime.putWaitTimeOf(STEP_WAIT_MS);
    }
}
