package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;

public class ClickLoginButton implements Task {

    public static ClickLoginButton now() {
        return new ClickLoginButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(ClickOnElement.on(LoginPage.LOGIN_BUTTON));
    }
}
