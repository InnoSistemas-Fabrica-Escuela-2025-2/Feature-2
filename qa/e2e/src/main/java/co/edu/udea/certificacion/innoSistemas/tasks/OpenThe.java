package co.edu.udea.certificacion.innoSistemas.tasks;

import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Open;

public class OpenThe implements Task {
    private OpenThe(){}
    PageObject page;
    public OpenThe (PageObject page){
        this.page = page;
    }
    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1000);
        actor.attemptsTo(Open.browserOn(page));
        WaitTime.putWaitTimeOf(1000);
    }

    public static OpenThe navigator(PageObject page){
        return Tasks.instrumented(OpenThe.class, page);
    }
}