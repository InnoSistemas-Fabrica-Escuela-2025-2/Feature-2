package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

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
        Target optionTarget = Target.the("dropdown option " + option)
                .located(By.xpath("//div[@role='option']//span[text()='" + option + "']"));
        actor.attemptsTo(
                ClickOnElement.on(optionTarget));
    }
}
