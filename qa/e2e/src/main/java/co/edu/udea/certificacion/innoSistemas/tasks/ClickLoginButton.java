package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class ClickLoginButton implements Task {

    public static ClickLoginButton now() {
        return new ClickLoginButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(LoginPage.LOGIN_BUTTON));
        WaitTime.putWaitTimeOf(1200);
    }
}
