package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class ClickNewTaskButton implements Task {

    public static ClickNewTaskButton now() {
        return new ClickNewTaskButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        WebElement addTaskBtn = TasksPage.ADD_TASK_BUTTON.resolveFor(actor);
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", addTaskBtn);
        WaitTime.putWaitTimeOf(1200);
        js.executeScript("arguments[0].click();", addTaskBtn);
        WaitTime.putWaitTimeOf(1200);
    }
}
