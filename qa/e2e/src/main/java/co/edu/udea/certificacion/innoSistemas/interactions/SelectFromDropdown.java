package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.actions.SelectFromOptions;

public class SelectFromDropdown implements Interaction {

    private final Target dropdown;
    private final String option;

    public SelectFromDropdown(Target dropdown, String option) {
        this.dropdown = dropdown;
        this.option = option;
    }

    public static SelectFromDropdown option(String option, Target dropdown) {
        return new SelectFromDropdown(dropdown, option);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                SelectFromOptions.byVisibleText(option).from(dropdown));
    }
}
