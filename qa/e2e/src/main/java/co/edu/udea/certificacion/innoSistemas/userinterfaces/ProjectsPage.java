package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ProjectsPage {

        public static final Target CREATE_PROJECT_BUTTON = Target.the("create project button")
                        .located(By.xpath("//*[@id=\"main-content\"]/div/div[1]/button"));

        public static final Target PROJECT_NAME_INPUT = Target.the("project name input")
                        .located(By.xpath("//*[@id=\"nombre\"]"));

        public static final Target PROJECT_DESCRIPTION_INPUT = Target.the("project description input")
                        .located(By.xpath("//*[@id=\"descripcion\"]"));

        public static final Target PROJECT_OBJECTIVES_INPUT = Target.the("project objectives input")
                        .located(By.xpath("//*[@id=\"objetivos\"]"));

        public static final Target DELIVERY_DATE_INPUT = Target.the("delivery date input")
                        .located(By.xpath("//*[@id=\"fechaEntrega\"]"));

        public static final Target TEAM_DROPDOWN = Target.the("team dropdown")
                        .located(By.xpath("//*[@id=\"equipo\"]"));

        public static final Target SUBMIT_PROJECT_BUTTON = Target.the("submit project button")
                        .located(By.xpath("//*[@id=\"radix-:r4:\"]/form/div[6]/button[2]"));

        public static final Target SUCCESS_MESSAGE = Target.the("success message")
                        .located(By.className("success-message"));

        public static final Target VALIDATION_ERRORS = Target.the("validation errors")
                        .located(By.className("validation-error"));

        public static final Target PROJECT_CARD = Target.the("project card with name {0}")
                        .locatedBy("//div[contains(@class,'project-card')]//h3[text()='{0}']");

        public static final Target PROJECT_MODAL = Target.the("project creation modal")
                        .located(By.className("project-modal"));
}
