package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ProjectsPage {

        public static final Target PROJECTS_LINK = Target.the("projects navigation link")
                        .located(By.xpath("//*[@id=\"root\"]/div[2]/div/aside/nav/ul/li[2]/a"));

        public static final Target CREATE_PROJECT_BUTTON = Target.the("create project button")
                        .located(By.xpath("//*[@id=\"main-content\"]/div/div[1]/button"));

        public static final Target PROJECT_NAME_INPUT = Target.the("project name input")
                        .located(By.id("nombre"));

        public static final Target PROJECT_DESCRIPTION_INPUT = Target.the("project description input")
                        .located(By.id("descripcion"));

        public static final Target PROJECT_OBJECTIVES_INPUT = Target.the("project objectives input")
                        .located(By.id("objetivos"));

        public static final Target DELIVERY_DATE_INPUT = Target.the("delivery date input")
                        .located(By.id("fechaEntrega"));

        public static final Target TEAM_DROPDOWN = Target.the("team dropdown")
                        .located(By.id("equipo"));

        public static final Target SUBMIT_PROJECT_BUTTON = Target.the("submit project button")
                        .located(By.xpath("//button[contains(text(), 'Crear Proyecto')]"));

        public static final Target CANCEL_PROJECT_BUTTON = Target.the("cancel project button")
                        .located(By.xpath("//*[@id=\"radix-:r4:\"]/form/div[7]/button[1]"));

        public static final Target CONFIRM_CANCEL_BUTTON = Target.the("confirm cancel button")
                        .located(By.xpath("//*[@id=\"radix-:r7:\"]/div[2]/button[2]"));

        public static final Target VALIDATION_ERROR = Target.the("validation error message")
                        .located(By.xpath("//*[@id=\"radix-:r4:\"]/form/div[1]/div"));
}
