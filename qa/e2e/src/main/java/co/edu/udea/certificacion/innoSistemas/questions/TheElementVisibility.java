package co.edu.udea.certificacion.innoSistemas.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Visibility;
import net.serenitybdd.screenplay.targets.Target;

public class TheElementVisibility implements Question<Boolean> {

    private final Target target;

    public TheElementVisibility(Target target) {
        this.target = target;
    }

    public static TheElementVisibility of(Target target) {
        return new TheElementVisibility(target);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return Visibility.of(target).answeredBy(actor);
    }
}
