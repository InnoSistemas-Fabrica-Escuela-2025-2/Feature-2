package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class SelectAnyResponsible implements Task {

    public static SelectAnyResponsible now() {
        return new SelectAnyResponsible();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(TasksPage.RESPONSIBLE_DROPDOWN));

        Target firstResponsible = Target.the("first responsible option")
                .located(By.xpath("//div[@role='option']//span[1]"));
        actor.attemptsTo(ClickOnElement.on(firstResponsible));
        WaitTime.putWaitTimeOf(1200);
    }

}
