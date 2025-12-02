package co.edu.udea.certificacion.innoSistemas.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;

public class TheElementText implements Question<String> {

    private final Target target;

    public TheElementText(Target target) {
        this.target = target;
    }

    public static TheElementText of(Target target) {
        return new TheElementText(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        return Text.of(target).answeredBy(actor);
    }
}
