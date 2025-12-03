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
                        .located(By.xpath("//*[@id=\"login-error\"]"));

        public static final Target USER_NAME = Target.the("user name displayed after login")
                        .located(By.xpath("//*[@id=\"root\"]/div[2]/header/nav/div[2]/span"));

        public static final Target LOGIN_PAGE = Target.the("login page")
                        .located(By.xpath("//*[@id=\"main-content\"]/div[1]/h3"));
}
