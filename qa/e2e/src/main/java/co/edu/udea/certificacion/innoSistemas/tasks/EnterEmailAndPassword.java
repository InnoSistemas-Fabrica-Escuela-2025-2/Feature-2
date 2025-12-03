package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.waits.WaitUntil;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.LoginPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class EnterEmailAndPassword implements Task {

    private final String email;
    private final String password;

    private EnterEmailAndPassword(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static EnterEmailAndPassword with(String email, String password) {
        return new EnterEmailAndPassword(email, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(
                WaitUntil.the(LoginPage.EMAIL_INPUT, isVisible()).forNoMoreThan(10).seconds(),
                FillInputField.in(LoginPage.EMAIL_INPUT, email),
                FillInputField.in(LoginPage.PASSWORD_INPUT, password)
        );
        WaitTime.putWaitTimeOf(1200);
    }
}
