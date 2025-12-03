package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.questions.TheElementVisibility;
import co.edu.udea.certificacion.innoSistemas.questions.TheElementText;
import co.edu.udea.certificacion.innoSistemas.tasks.*;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

public class TaskManagementSteps {

    @Managed
    WebDriver driver;

    private static final int STEP_WAIT_MS = 1200;

    private Actor student;
    private TaskModel currentTask;

    @Before
    public void setUp() {
        student = Actor.named("Student");
        student.can(BrowseTheWeb.with(driver));
    }

    @When("the user opens the project {string}")
    public void userOpensProject(String projectName) {
        student.attemptsTo(OpenProjectByName.called(projectName));
    }

    @When("the user clicks on new task button")
    public void userClicksNewTaskButton() {
        student.attemptsTo(ClickNewTaskButton.now());
    }

    @When("the user fills the task form with:")
    public void userFillsTaskForm(DataTable dataTable) {
        currentTask = TaskModel.fromDataTable(dataTable);
        student.attemptsTo(FillTaskForm.with(currentTask));
    }

    @When("the user selects any responsible person")
    public void userSelectsAnyResponsible() {
        student.attemptsTo(SelectAnyResponsible.now());
    }
    
    @When("the user sets the task due date")
    public void userSetsTaskDueDate() {
        student.attemptsTo(SetTaskDueDate.to(currentTask.getDueDate()));
    }

    @When("the user submits the task form")
    public void userSubmitsTaskForm() {
        student.attemptsTo(SubmitTaskForm.now());
    }

    @Then("the task should appear in the task list")
    public void taskShouldAppearInList() {
        WaitTime.putWaitTimeOf(STEP_WAIT_MS);
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.SUCCESS_TOAST), is(true)),
                seeThat(TheElementText.of(TasksPage.SUCCESS_TOAST), containsString("Tarea creada exitosamente")));
    }
}
