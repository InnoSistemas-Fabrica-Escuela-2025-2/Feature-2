package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.actions.Click;

public class ClickOnElement implements Interaction {

    private final Target target;

    public ClickOnElement(Target target) {
        this.target = target;
    }

    public static ClickOnElement on(Target target) {
        return new ClickOnElement(target);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(target));
    }
}
