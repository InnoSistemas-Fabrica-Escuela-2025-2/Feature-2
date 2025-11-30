package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;

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
        actor.attemptsTo(
                FillInputField.in(LoginPage.USERNAME_INPUT, username),
                FillInputField.in(LoginPage.PASSWORD_INPUT, password),
                ClickOnElement.on(LoginPage.LOGIN_BUTTON));
    }
}
