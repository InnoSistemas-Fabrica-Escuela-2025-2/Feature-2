package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.annotations.Managed;
import org.openqa.selenium.WebDriver;
import co.edu.udea.certificacion.innoSistemas.tasks.*;
import co.edu.udea.certificacion.innoSistemas.questions.*;
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.interactions.SetDateField;
import org.openqa.selenium.By;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

public class TaskManagementSteps {

    @Managed
    WebDriver driver;

    private Actor student;
    private TaskModel currentTask;
    private String currentProjectName;

    @Before
    public void setUp() {
        student = Actor.named("Student");
        student.can(BrowseTheWeb.with(driver));
    }

    @When("the user opens the project {string}")
    public void userOpensProject(String projectName) {
        currentProjectName = projectName;
        
        // Click on Projects navigation link
        student.attemptsTo(ClickOnElement.on(ProjectsPage.PROJECTS_LINK));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Scroll and click on the project card using JavaScript to avoid interception
        Target projectCard = TasksPage.PROJECT_CARD_LINK.of(projectName);
        student.attemptsTo(Scroll.to(projectCard));
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Use JavaScript click to avoid element click intercepted error
        org.openqa.selenium.WebElement element = projectCard.resolveFor(student);
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) BrowseTheWeb.as(student).getDriver();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        js.executeScript("arguments[0].click();", element);
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the user clicks on new task button")
    public void userClicksNewTaskButton() {
        student.attemptsTo(ClickOnElement.on(TasksPage.ADD_TASK_BUTTON));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the user fills the task form with:")
    public void userFillsTaskForm(DataTable dataTable) {
        currentTask = TaskModel.fromDataTable(dataTable);
        
        // Fill title and description
        student.attemptsTo(
                FillInputField.in(TasksPage.TASK_TITLE_INPUT, currentTask.getName()),
                FillInputField.in(TasksPage.TASK_DESCRIPTION_INPUT, currentTask.getDescription())
        );
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Select project from dropdown
        student.attemptsTo(ClickOnElement.on(TasksPage.PROJECT_DROPDOWN));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student.attemptsTo(SelectFromDropdown.option(currentTask.getProject(), TasksPage.PROJECT_DROPDOWN));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the user selects any responsible person")
    public void userSelectsAnyResponsible() {
        // Click to open responsible dropdown
        student.attemptsTo(ClickOnElement.on(TasksPage.RESPONSIBLE_DROPDOWN));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Click on first option available (using span text inside option)
        Target firstResponsible = Target.the("first responsible option")
                .located(By.xpath("//div[@role='option']//span[1]"));
        student.attemptsTo(ClickOnElement.on(firstResponsible));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @When("the user sets the task due date")
    public void userSetsTaskDueDate() {
        // Set due date after selecting responsible
        student.attemptsTo(SetDateField.to(TasksPage.DUE_DATE_INPUT, currentTask.getDueDate()));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the user submits the task form")
    public void userSubmitsTaskForm() {
        student.attemptsTo(ClickOnElement.on(TasksPage.SUBMIT_TASK_BUTTON));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("the task should appear in the task list")
    public void taskShouldAppearInList() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_ITEM.of(currentTask.getName())), is(true)));
    }

    @Then("a confirmation message should be displayed")
    public void confirmationMessageShouldBeDisplayed() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.CONFIRMATION_MESSAGE), is(true)));
    }

    @Then("validation errors should be shown")
    public void validationErrorsShouldBeShown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.VALIDATION_ERRORS), is(true)));
    }

    @Then("the task should not be created")
    public void taskShouldNotBeCreated() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student.should(
                seeThat(TheElementVisibility.of(TasksPage.TASK_MODAL), is(true)));
    }
}
