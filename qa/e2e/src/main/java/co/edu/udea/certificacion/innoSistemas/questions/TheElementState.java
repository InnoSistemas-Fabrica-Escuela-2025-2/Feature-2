package co.edu.udea.certificacion.innoSistemas.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Enabled;
import net.serenitybdd.screenplay.targets.Target;

public class TheElementState implements Question<Boolean> {

    private final Target target;

    public TheElementState(Target target) {
        this.target = target;
    }

    public static TheElementState isEnabled(Target target) {
        return new TheElementState(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return Enabled.of(target).answeredBy(actor);
    }
}
