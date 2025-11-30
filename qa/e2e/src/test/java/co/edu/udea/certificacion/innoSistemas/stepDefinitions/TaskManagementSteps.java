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
        // Aqui se podria crear el proyecto si no existe
        // o simplemente asumir que ya existe desde el background
    }

    @Given("the user has opened the project dashboard")
    public void userHasOpenedProjectDashboard() {
        student.attemptsTo(
                NavigateTo.theDashboard());
    }

    @Given("a task {string} exists with status {string}")
    public void taskExistsWithStatus(String taskName, String status) {
        currentTask = new TaskModel(taskName, null, status, null, null);
        // Aqui se crearia la tarea si no existe
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
                SelectFromDropdown.option(currentTask.getStatus(), TasksPage.TASK_STATUS_DROPDOWN),
                SelectFromDropdown.option(currentTask.getPriority(), TasksPage.TASK_PRIORITY_DROPDOWN),
                FillInputField.in(TasksPage.DUE_DATE_INPUT, currentTask.getDueDate()));
    }

    @When("the user saves the task")
    public void userSavesTask() {
        student.attemptsTo(
                ClickOnElement.on(TasksPage.SAVE_TASK_BUTTON));
    }

    @When("the user enters task name {string}")
    public void userEntersTaskName(String taskName) {
        currentTask = new TaskModel(taskName, null, null, null, null);
        student.attemptsTo(
                FillInputField.in(TasksPage.TASK_NAME_INPUT, taskName));
    }

    @When("the user selects status {string}")
    public void userSelectsStatus(String status) {
        student.attemptsTo(
                SelectFromDropdown.option(status, TasksPage.TASK_STATUS_DROPDOWN));
    }

    @When("the user clicks on the task")
    public void userClicksOnTask() {
        student.attemptsTo(
                ClickOnElement.on(TasksPage.TASK_ITEM.of(currentTask.getName())));
    }

    @When("the user changes status to {string}")
    public void userChangesStatusTo(String newStatus) {
        student.attemptsTo(
                SelectFromDropdown.option(newStatus, TasksPage.TASK_STATUS_DROPDOWN));
    }

    @When("the user saves the changes")
    public void userSavesChanges() {
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

    @Then("the task status should show {string}")
    public void taskStatusShouldShow(String expectedStatus) {
        student.should(
                seeThat(TheElementText.of(TasksPage.TASK_STATUS_BADGE), equalTo(expectedStatus)));
    }

    @Then("the task {string} should be visible")
    public void taskShouldBeVisible(String taskName) {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_ITEM.of(taskName)), is(true)));
    }

    @Then("the task should appear with default priority")
    public void taskShouldAppearWithDefaultPriority() {
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_PRIORITY_BADGE), is(true)));
    }

    @Then("the task status should be updated to {string}")
    public void taskStatusShouldBeUpdatedTo(String status) {
        student.should(
                seeThat(TheElementText.of(TasksPage.TASK_STATUS_BADGE), equalTo(status)));
    }

    @Then("the dashboard should reflect the change")
    public void dashboardShouldReflectChange() {
        // Verificar que el dashboard se actualizo
        student.should(
                seeThat(TheCurrentUrl.value(), containsString("/dashboard")));
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
