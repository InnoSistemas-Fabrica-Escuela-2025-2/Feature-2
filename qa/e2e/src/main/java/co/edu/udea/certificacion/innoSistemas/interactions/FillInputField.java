package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Enter;

public class FillInputField implements Interaction {

    private final Target target;
    private final String value;

    public FillInputField(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    public static FillInputField in(Target target, String value) {
        return new FillInputField(target, value);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Clear.field(target),
                Enter.theValue(value).into(target));
    }
}
