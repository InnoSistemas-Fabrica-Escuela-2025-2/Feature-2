package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.interactions.SetDateField;
import co.edu.udea.certificacion.innoSistemas.questions.TheElementVisibility;
import net.serenitybdd.screenplay.targets.Target;

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

    @When("the user opens the project {string}")
    public void userOpensProject(String projectName) {
        student.attemptsTo(ClickOnElement.on(ProjectsPage.PROJECTS_LINK));
        waitFor(3000);
        
        Target projectCard = TasksPage.PROJECT_CARD_LINK.of(projectName);
        student.attemptsTo(Scroll.to(projectCard));
        waitFor(2000);
        
        // Use JavaScript click to avoid navbar interception
        WebElement element = projectCard.resolveFor(student);
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(student).getDriver();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        waitFor(500);
        js.executeScript("arguments[0].click();", element);
        waitFor(5000);
    }

    @When("the user clicks on new task button")
    public void userClicksNewTaskButton() {
        student.attemptsTo(ClickOnElement.on(TasksPage.ADD_TASK_BUTTON));
        waitFor(5000);
    }

    @When("the user fills the task form with:")
    public void userFillsTaskForm(DataTable dataTable) {
        currentTask = TaskModel.fromDataTable(dataTable);
        
        student.attemptsTo(
                FillInputField.in(TasksPage.TASK_TITLE_INPUT, currentTask.getName()),
                FillInputField.in(TasksPage.TASK_DESCRIPTION_INPUT, currentTask.getDescription())
        );
        waitFor(3000);
        
        student.attemptsTo(ClickOnElement.on(TasksPage.PROJECT_DROPDOWN));
        waitFor(3000);
        student.attemptsTo(SelectFromDropdown.option(currentTask.getProject(), TasksPage.PROJECT_DROPDOWN));
        waitFor(3000);
    }

    @When("the user selects any responsible person")
    public void userSelectsAnyResponsible() {
        student.attemptsTo(ClickOnElement.on(TasksPage.RESPONSIBLE_DROPDOWN));
        waitFor(3000);
        
        Target firstResponsible = Target.the("first responsible option")
                .located(By.xpath("//div[@role='option']//span[1]"));
        student.attemptsTo(ClickOnElement.on(firstResponsible));
        waitFor(3000);
    }
    
    @When("the user sets the task due date")
    public void userSetsTaskDueDate() {
        student.attemptsTo(SetDateField.to(TasksPage.DUE_DATE_INPUT, currentTask.getDueDate()));
        waitFor(3000);
    }

    @When("the user submits the task form")
    public void userSubmitsTaskForm() {
        student.attemptsTo(ClickOnElement.on(TasksPage.SUBMIT_TASK_BUTTON));
        waitFor(5000);
    }

    @Then("the task should appear in the task list")
    public void taskShouldAppearInList() {
        waitFor(3000);
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.SUCCESS_TOAST), is(true)));
    }
    
    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
