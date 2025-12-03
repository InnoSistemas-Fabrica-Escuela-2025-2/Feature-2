package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;

public class ClickNewTaskButton implements Task {

    public static ClickNewTaskButton now() {
        return new ClickNewTaskButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebElement addTaskBtn = TasksPage.ADD_TASK_BUTTON.resolveFor(actor);
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", addTaskBtn);
        sleep(500);
        js.executeScript("arguments[0].click();", addTaskBtn);
        sleep(5000);
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
