package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginPage {

    public static final Target USERNAME_INPUT = Target.the("username input field")
            .located(By.id("username"));

    public static final Target PASSWORD_INPUT = Target.the("password input field")
            .located(By.id("password"));

    public static final Target LOGIN_BUTTON = Target.the("login button")
            .located(By.cssSelector("button[type='submit']"));

    public static final Target ERROR_MESSAGE = Target.the("error message")
            .located(By.className("error-message"));

    public static final Target USERNAME_VALIDATION = Target.the("username validation message")
            .located(By.id("username-error"));
}
