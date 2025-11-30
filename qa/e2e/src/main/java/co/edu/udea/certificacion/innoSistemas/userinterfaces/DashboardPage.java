package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class DashboardPage {

        public static final Target WELCOME_MESSAGE = Target.the("welcome message")
                        .located(By.className("welcome-message"));

        public static final Target PROJECTS_LINK = Target.the("projects navigation link")
                        .located(By.xpath("//*[@id=\"root\"]/div[2]/div/aside/nav/ul/li[2]/a"));
}
