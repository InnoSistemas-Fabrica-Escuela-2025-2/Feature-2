package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginPage {

        public static final Target EMAIL_INPUT = Target.the("email input field")
                        .located(By.xpath("//*[@id=\"email\"]"));

        public static final Target PASSWORD_INPUT = Target.the("password input field")
                        .located(By.xpath("//*[@id=\"password\"]"));

        public static final Target LOGIN_BUTTON = Target.the("login button")
                        .located(By.xpath("//*[@id=\"main-content\"]/div[2]/form/button"));

        public static final Target ERROR_MESSAGE = Target.the("error message")
                        .located(By.className("error-message"));

        public static final Target EMAIL_VALIDATION = Target.the("email validation message")
                        .located(By.id("email-error"));
}
