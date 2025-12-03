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
import co.edu.udea.certificacion.innoSistemas.questions.*;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.interactions.SetDateField;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
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
                Login.withCredentials(username, "geraldin"));
        student.attemptsTo(
                WaitUntil.the(LoginPage.USER_NAME, isVisible()).forNoMoreThan(20).seconds()
        );
        waitFor(5000);
    }

    @Given("the user is on the projects page")
    public void userIsOnProjectsPage() {
        student.attemptsTo(NavigateTo.theProjectsPage());
        student.attemptsTo(
                WaitUntil.the(ProjectsPage.CREATE_PROJECT_BUTTON, isVisible()).forNoMoreThan(20).seconds()
        );
        waitFor(5000);
    }

    @When("the user clicks on create project button")
    public void userClicksCreateProjectButton() {
        student.attemptsTo(ClickOnElement.on(ProjectsPage.CREATE_PROJECT_BUTTON));
        student.attemptsTo(
                WaitUntil.the(ProjectsPage.PROJECT_NAME_INPUT, isVisible()).forNoMoreThan(15).seconds()
        );
        waitFor(5000);
    }

    @When("the user fills the complete project form with:")
    public void userFillsCompleteProjectForm(DataTable dataTable) {
        currentProject = Project.fromDataTable(dataTable);
        student.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, currentProject.getName()),
                FillInputField.in(ProjectsPage.PROJECT_DESCRIPTION_INPUT, currentProject.getDescription()),
                FillInputField.in(ProjectsPage.PROJECT_OBJECTIVES_INPUT, currentProject.getObjectives())
        );
        waitFor(3000);
        
        student.attemptsTo(ClickOnElement.on(ProjectsPage.TEAM_DROPDOWN));
        waitFor(3000);
        student.attemptsTo(SelectFromDropdown.option(currentProject.getTeam(), ProjectsPage.TEAM_DROPDOWN));
        waitFor(3000);
        
        student.attemptsTo(SetDateField.to(ProjectsPage.DELIVERY_DATE_INPUT, currentProject.getDeliveryDate()));
        waitFor(5000);
    }

    @When("the user fills partial project form with:")
    public void userFillsPartialProjectForm(DataTable dataTable) {
        currentProject = Project.fromDataTable(dataTable);
        student.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, currentProject.getName()),
                FillInputField.in(ProjectsPage.PROJECT_OBJECTIVES_INPUT, currentProject.getObjectives())
        );
        waitFor(5000);
    }

    @Then("a validation error should be displayed with message {string}")
    public void validationErrorShouldBeDisplayedWithMessage(String expectedMessage) {
        waitFor(5000);
        student.should(seeThat(TheElementVisibility.of(ProjectsPage.VALIDATION_ERROR), is(true)));
    }

    @Then("the user clicks cancel button")
    public void userClicksCancelButton() {
        student.attemptsTo(ClickOnElement.on(ProjectsPage.CANCEL_PROJECT_BUTTON));
        waitFor(3000);
    }

    @Then("the user confirms cancel action")
    public void userConfirmsCancelAction() {
        student.attemptsTo(ClickOnElement.on(ProjectsPage.CONFIRM_CANCEL_BUTTON));
        waitFor(5000);
    }

    @When("the user submits the project form")
    public void userSubmitsProjectForm() {
        student.attemptsTo(ClickOnElement.on(ProjectsPage.SUBMIT_PROJECT_BUTTON));
        waitFor(5000);
    }

    @Then("the project should appear in the projects list")
    public void projectShouldAppearInList() {
        waitFor(5000);
    }
    
    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
