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
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

public class TaskManagementSteps {

    @Managed
    WebDriver driver;

    private Actor student;
    private TaskModel currentTask;

    @Before
    public void setUp() {
        student = Actor.named("Student");
        student.can(BrowseTheWeb.with(driver));
    }

    @Given("a project named {string} exists")
    public void projectExists(String projectName) {
        // Assuming project exists or created in background
    }

    @Given("the user has opened the project dashboard")
    public void userHasOpenedProjectDashboard() {
        student.attemptsTo(
                NavigateTo.theDashboard());
    }

    @Given("the user is inside the project {string}")
    public void userIsInsideProject(String projectName) {
        student.attemptsTo(
                NavigateToProject.named(projectName));
    }

    @When("the user clicks on add task button")
    public void userClicksAddTaskButton() {
        student.attemptsTo(
                ClickOnElement.on(TasksPage.ADD_TASK_BUTTON));
    }

    @When("the user fills the task form with:")
    public void userFillsTaskForm(DataTable dataTable) {
        currentTask = TaskModel.fromDataTable(dataTable);
        student.attemptsTo(
                FillInputField.in(TasksPage.TASK_NAME_INPUT, currentTask.getName()),
                FillInputField.in(TasksPage.TASK_DESCRIPTION_INPUT, currentTask.getDescription()),
                SelectFromDropdown.option(currentTask.getProject(), TasksPage.PROJECT_DROPDOWN),
                SelectFromDropdown.option(currentTask.getResponsible(), TasksPage.RESPONSIBLE_DROPDOWN),
                FillInputField.in(TasksPage.DUE_DATE_INPUT, currentTask.getDueDate()));
    }

    @When("the user saves the task")
    public void userSavesTask() {
        student.attemptsTo(
                ClickOnElement.on(TasksPage.SAVE_TASK_BUTTON));
    }

    @Then("the task should appear in the task list")
    public void taskShouldAppearInList() {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_ITEM.of(currentTask.getName())), is(true)));
    }

    @Then("a confirmation message should be displayed")
    public void confirmationMessageShouldBeDisplayed() {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.CONFIRMATION_MESSAGE), is(true)));
    }

    @Then("validation errors should be shown")
    public void validationErrorsShouldBeShown() {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.VALIDATION_ERRORS), is(true)));
    }

    @Then("the task should not be created")
    public void taskShouldNotBeCreated() {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_MODAL), is(true)));
    }
}
