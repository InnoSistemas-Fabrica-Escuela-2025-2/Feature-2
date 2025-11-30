package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import co.edu.udea.certificacion.innoSistemas.tasks.*;
import co.edu.udea.certificacion.innoSistemas.questions.*;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

public class ProjectCreationSteps {

    @Managed
    WebDriver driver;

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
                Login.withCredentials(username, "Test123!"));
    }

    @Given("the user is on the projects page")
    public void userIsOnProjectsPage() {
        student.attemptsTo(
                NavigateTo.theProjectsPage());
    }

    @When("the user clicks on create project button")
    public void userClicksCreateProjectButton() {
        student.attemptsTo(
                ClickOnElement.on(ProjectsPage.CREATE_PROJECT_BUTTON));
    }

    @When("the user fills the project form with:")
    public void userFillsProjectForm(DataTable dataTable) {
        currentProject = Project.fromDataTable(dataTable);
        student.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, currentProject.getName()),
                FillInputField.in(ProjectsPage.PROJECT_DESCRIPTION_INPUT, currentProject.getDescription()),
                FillInputField.in(ProjectsPage.START_DATE_INPUT, currentProject.getStartDate()),
                FillInputField.in(ProjectsPage.END_DATE_INPUT, currentProject.getEndDate()));
    }

    @When("the user submits the project form")
    public void userSubmitsProjectForm() {
        student.attemptsTo(
                ClickOnElement.on(ProjectsPage.SUBMIT_PROJECT_BUTTON));
    }

    @When("the user fills the project name with {string}")
    public void userFillsProjectName(String projectName) {
        currentProject = new Project(projectName, null, null, null);
        student.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, projectName));
    }

    @When("the user fills the start date with {string}")
    public void userFillsStartDate(String startDate) {
        student.attemptsTo(
                FillInputField.in(ProjectsPage.START_DATE_INPUT, startDate));
    }

    @Then("the project should appear in the projects list")
    public void projectShouldAppearInList() {
        student.should(
                seeThat(TheElementVisibility.of(ProjectsPage.PROJECT_CARD.of(currentProject.getName())), is(true)));
    }

    @Then("a success message should be displayed")
    public void successMessageShouldBeDisplayed() {
        student.should(
                seeThat(TheElementVisibility.of(ProjectsPage.SUCCESS_MESSAGE), is(true)));
    }

    @Then("the project name should be {string}")
    public void projectNameShouldBe(String expectedName) {
        student.should(
                seeThat(TheElementText.of(ProjectsPage.PROJECT_CARD.of(expectedName)), containsString(expectedName)));
    }

    @Then("the project {string} should be created")
    public void projectShouldBeCreated(String projectName) {
        student.should(
                seeThat(TheElementVisibility.of(ProjectsPage.PROJECT_CARD.of(projectName)), is(true)));
    }

    @Then("validation errors should be displayed")
    public void validationErrorsShouldBeDisplayed() {
        student.should(
                seeThat(TheElementVisibility.of(ProjectsPage.VALIDATION_ERRORS), is(true)));
    }

    @Then("the project should not be created")
    public void projectShouldNotBeCreated() {
        student.should(
                seeThat(TheElementVisibility.of(ProjectsPage.PROJECT_MODAL), is(true)));
    }
}
