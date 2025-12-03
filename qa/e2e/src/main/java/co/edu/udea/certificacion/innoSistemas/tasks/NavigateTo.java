package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.SystemEnvironmentVariables;

public class NavigateTo implements Task {

    private final String path;

    public NavigateTo(String path) {
        this.path = path;
    }

    public static NavigateTo theLoginPage() {
        return new NavigateTo("/login");
    }

    public static NavigateTo theProjectsPage() {
        return new NavigateTo("/proyectos");
    }

    public static NavigateTo theDashboard() {
        return new NavigateTo("/dashboard");
    }

    public static NavigateTo theTasksPage() {
        return new NavigateTo("/tareas");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();
        String baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("webdriver.base.url");
        actor.attemptsTo(
                Open.url(baseUrl + path));
    }
}
