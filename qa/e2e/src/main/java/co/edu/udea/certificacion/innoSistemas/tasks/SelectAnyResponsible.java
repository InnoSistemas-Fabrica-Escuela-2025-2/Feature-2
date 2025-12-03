package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;

public class SelectAnyResponsible implements Task {

    public static SelectAnyResponsible now() {
        return new SelectAnyResponsible();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(ClickOnElement.on(TasksPage.RESPONSIBLE_DROPDOWN));
        sleep(3000);

        Target firstResponsible = Target.the("first responsible option")
                .located(By.xpath("//div[@role='option']//span[1]"));
        actor.attemptsTo(ClickOnElement.on(firstResponsible));
        sleep(3000);
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
