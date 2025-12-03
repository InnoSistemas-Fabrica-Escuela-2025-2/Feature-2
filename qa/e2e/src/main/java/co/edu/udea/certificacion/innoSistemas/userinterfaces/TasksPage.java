package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class TasksPage {

        public static final Target PROJECT_CARD_LINK = Target.the("project card link with name {0}")
                        .locatedBy("//a[contains(@class, 'hover:text-primary') and contains(text(), '{0}')]");

        public static final Target ADD_TASK_BUTTON = Target.the("add task button")
                        .located(By.xpath("//*[@id=\"main-content\"]/div/div[3]/div[1]/div[1]/div/button[3]"));

        public static final Target TASK_TITLE_INPUT = Target.the("task title input")
                        .located(By.id("titulo"));

        public static final Target TASK_DESCRIPTION_INPUT = Target.the("task description input")
                        .located(By.id("descripcion"));

        public static final Target PROJECT_DROPDOWN = Target.the("project dropdown")
                        .located(By.id("proyecto"));

        public static final Target RESPONSIBLE_DROPDOWN = Target.the("responsible dropdown")
                        .located(By.id("responsable"));

        public static final Target DUE_DATE_INPUT = Target.the("due date input")
                        .located(By.id("fecha"));

        public static final Target SUBMIT_TASK_BUTTON = Target.the("submit task button")
                        .located(By.xpath("//button[contains(text(), 'Crear Tarea')]"));

        public static final Target SUCCESS_TOAST = Target.the("success toast message")
                        .located(By.xpath("//*[contains(text(), 'Tarea creada exitosamente')]"));

        public static final Target TASK_ITEM = Target.the("task item with name {0}")
                        .locatedBy("//*[@id='radix-:rd:-content-all']//div[contains(text(), '{0}')]");

        public static final Target TASK_MODAL = Target.the("task creation modal")
                        .located(By.className("task-modal"));

        public static final Target VALIDATION_ERRORS = Target.the("validation errors")
                        .located(By.className("validation-error"));
}
