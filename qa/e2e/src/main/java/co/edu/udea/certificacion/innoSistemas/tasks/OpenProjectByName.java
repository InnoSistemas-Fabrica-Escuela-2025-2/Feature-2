package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;

public class OpenProjectByName implements Task {

    private final String projectName;

    private OpenProjectByName(String projectName) {
        this.projectName = projectName;
    }

    public static OpenProjectByName called(String projectName) {
        return new OpenProjectByName(projectName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.PROJECTS_LINK));
        sleep(3000);

        Target projectCard = TasksPage.PROJECT_CARD_LINK.of(projectName);
        actor.attemptsTo(Scroll.to(projectCard));
        sleep(2000);

        WebElement element = projectCard.resolveFor(actor);
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        sleep(500);
        js.executeScript("arguments[0].click();", element);
        sleep(5000);
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
