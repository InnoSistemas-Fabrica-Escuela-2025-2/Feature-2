package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class TasksPage {

        public static final Target ADD_TASK_BUTTON = Target.the("add task button")
                        .located(By.xpath("//*[@id=\"main-content\"]/div/div[3]/div[1]/div[1]/div/button[3]"));

        public static final Target TASK_NAME_INPUT = Target.the("task name input")
                        .located(By.xpath("//*[@id=\"titulo\"]"));

        public static final Target TASK_DESCRIPTION_INPUT = Target.the("task description input")
                        .located(By.xpath("//*[@id=\"descripcion\"]"));

        public static final Target PROJECT_DROPDOWN = Target.the("project dropdown")
                        .located(By.xpath("//*[@id=\"proyecto\"]"));

        public static final Target RESPONSIBLE_DROPDOWN = Target.the("responsible dropdown")
                        .located(By.xpath("//*[@id=\"responsable\"]"));

        public static final Target DUE_DATE_INPUT = Target.the("due date input")
                        .located(By.xpath("//*[@id=\"fecha\"]"));

        public static final Target SAVE_TASK_BUTTON = Target.the("save task button")
                        .located(By.xpath("//button[contains(text(), 'Crear Tarea')]"));

        public static final Target CONFIRMATION_MESSAGE = Target.the("confirmation message")
                        .located(By.className("confirmation-message"));

        public static final Target TASK_ITEM = Target.the("task item with name {0}")
                        .locatedBy("//div[contains(@class,'task-item')]//span[text()='{0}']");

        public static final Target TASK_MODAL = Target.the("task creation modal")
                        .located(By.className("task-modal"));

        public static final Target VALIDATION_ERRORS = Target.the("validation errors")
                        .located(By.className("validation-error"));
}
