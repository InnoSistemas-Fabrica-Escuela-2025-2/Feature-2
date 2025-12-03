package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.waits.WaitUntil;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class Login implements Task {

    private final String username;
    private final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Login withCredentials(String username, String password) {
        return new Login(username, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(
                WaitUntil.the(LoginPage.EMAIL_INPUT, isVisible()).forNoMoreThan(10).seconds(),
                FillInputField.in(LoginPage.EMAIL_INPUT, username),
                FillInputField.in(LoginPage.PASSWORD_INPUT, password),
                ClickOnElement.on(LoginPage.LOGIN_BUTTON));
        WaitTime.putWaitTimeOf(1200);
    }
}
