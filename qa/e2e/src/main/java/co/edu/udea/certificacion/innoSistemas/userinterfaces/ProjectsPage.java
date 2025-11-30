package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ProjectsPage {

    public static final Target CREATE_PROJECT_BUTTON = Target.the("create project button")
            .located(By.cssSelector("button[aria-label='Create Project']"));

    public static final Target PROJECT_NAME_INPUT = Target.the("project name input")
            .located(By.name("projectName"));

    public static final Target PROJECT_DESCRIPTION_INPUT = Target.the("project description input")
            .located(By.name("projectDescription"));

    public static final Target START_DATE_INPUT = Target.the("start date input")
            .located(By.name("startDate"));

    public static final Target END_DATE_INPUT = Target.the("end date input")
            .located(By.name("endDate"));

    public static final Target SUBMIT_PROJECT_BUTTON = Target.the("submit project button")
            .located(By.cssSelector("button[type='submit']"));

    public static final Target SUCCESS_MESSAGE = Target.the("success message")
            .located(By.className("success-message"));

    public static final Target VALIDATION_ERRORS = Target.the("validation errors")
            .located(By.className("validation-error"));

    public static final Target PROJECT_CARD = Target.the("project card with name {0}")
            .locatedBy("//div[contains(@class,'project-card')]//h3[text()='{0}']");

    public static final Target PROJECT_MODAL = Target.the("project creation modal")
            .located(By.className("project-modal"));
}
